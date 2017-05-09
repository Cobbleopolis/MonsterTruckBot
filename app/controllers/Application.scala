package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.{BotInstance, CoreSettings, User}
import discord.DiscordBot
import jsmessages.JsMessagesFactory
import play.api.Configuration
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc._
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import util.AuthUtil

import scala.concurrent.Future

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets, environment: RuntimeEnvironment, ws: WSClient, messages: MessagesApi, discordBot: DiscordBot, configuration: Configuration, authUtil: AuthUtil) extends Controller with SecureSocial with I18nSupport {

    val env: RuntimeEnvironment = environment

    val jsMessagesFactory = new JsMessagesFactory(messages)

    def index = UserAwareAction { implicit request => {
        implicit val userOpt: Option[User] = request.user.asInstanceOf[Option[User]]
        Ok(views.html.index())
    }
    }

    def servers: Action[AnyContent] = SecuredAction.async { implicit request => {
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        authUtil.getManageableServers(userOpt.get).map(userServers => Ok(views.html.servers(userServers)))
    }
    }

    def createBot: Action[AnyContent] = SecuredAction.async { implicit request =>
        Future {
            val guildOpt: Option[String] = request.getQueryString("guild_id")
            if (guildOpt.isDefined) {
                val botInstanceOpt: Option[BotInstance] = BotInstance.get(guildOpt.get)
                val coreSettingsOpt: Option[CoreSettings] = CoreSettings.get(guildOpt.get)
                if (botInstanceOpt.isEmpty)
                    BotInstance.insert(BotInstance(guildOpt.get))
                if (coreSettingsOpt.isEmpty)
                    CoreSettings.insert(CoreSettings(guildOpt.get))
                Redirect(routes.Dashboard.dashboard(guildOpt.get))
            } else
                Redirect(routes.Application.servers())
        }
    }

    def jsMessages(page: String) = Action {
        val jsMessages = if (page.nonEmpty && jsMessagesFactory.filtering(_.startsWith(page)).allMessages.nonEmpty)
            jsMessagesFactory.filtering(key => key.startsWith(page) || key.startsWith("error") || key.startsWith("format") || key.startsWith("constraint") || key.startsWith("global"))
        else
            jsMessagesFactory.all
        Ok(jsMessages.all(Some("window.Messages")))
    }

    override def messagesApi: MessagesApi = messages
}
package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.{BotInstance, User}
import com.cobble.bot.common.util.DiscordApiUtil
import com.cobble.bot.discord.DiscordBot
import jsmessages.JsMessagesFactory
import models.UserServersResponse
import models.UserServersResponse._
import play.api.Configuration
import play.api.db.Database
import play.api.i18n.MessagesApi
import play.api.libs.ws.WSClient
import play.api.mvc._
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import sx.blah.discord.handle.obj.Permissions

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets, environment: RuntimeEnvironment, ws: WSClient, messagesApi: MessagesApi, discordBot: DiscordBot, configuration: Configuration) extends Controller with SecureSocial {

    val env: RuntimeEnvironment = environment

    val jsMessagesFactory = new JsMessagesFactory(messagesApi)

    def index = UserAwareAction { implicit request => {
        implicit val userOpt: Option[User] = request.user.asInstanceOf[Option[User]]
        Ok(views.html.index())
    }
    }

    def servers: Action[AnyContent] = SecuredAction.async { implicit request => {
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        ws.url(DiscordApiUtil.CURRENT_USER_GUILDS).withHeaders("Authorization" -> s"${userOpt.get.tokenType} ${userOpt.get.accessToken}").get().map(res => {
            val servers: Array[UserServersResponse] = res.json.as[List[UserServersResponse]].toArray.sortBy(_.name)
            Ok(views.html.servers(servers.filter(s => s.owner || Permissions.MANAGE_SERVER.hasPermission(s.permissions))))
        })
    }
    }

    def dashboard(id: String) = SecuredAction { implicit request => {
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        val botInstanceOpt: Option[BotInstance] = BotInstance.get(id)
        if (botInstanceOpt.isDefined)
            Ok(views.html.dashboard(botInstanceOpt.get))
        else {
            Redirect(discordBot.getInviteLink(id, routes.Application.createBot().absoluteURL()))
        }
    }
    }

    def createBot = SecuredAction { implicit request => {
        val guildOpt: Option[String] = request.getQueryString("guild_id")
        if (guildOpt.isDefined) {
            val botInstanceOpt: Option[BotInstance] = BotInstance.get(guildOpt.get)
            if (botInstanceOpt.isEmpty) {
                BotInstance.insert[String](BotInstance(guildOpt.get))
                Redirect(routes.Application.dashboard(guildOpt.get))
            } else
                Redirect(routes.Application.dashboard(guildOpt.get))
        } else
            Redirect(routes.Application.servers())
    }
    }

    def messages(page: String) = Action {
        val jsMessages = if (page.nonEmpty && jsMessagesFactory.filtering(_.startsWith(page)).allMessages.nonEmpty)
            jsMessagesFactory.filtering(key => key.startsWith(page) || key.startsWith("error") || key.startsWith("format") || key.startsWith("constraint") || key.startsWith("global"))
        else
            jsMessagesFactory.all
        Ok(jsMessages.all(Some("window.Messages")))
    }

}
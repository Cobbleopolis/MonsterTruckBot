package controllers


import javax.inject.Inject

import discord.DiscordBot
import jsmessages.JsMessagesFactory
import play.api.Configuration
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc._
import securesocial.core.{BasicProfile, RuntimeEnvironment, SecureSocial}
import util.AuthUtil

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets, environment: RuntimeEnvironment, ws: WSClient, messages: MessagesApi, discordBot: DiscordBot, configuration: Configuration, authUtil: AuthUtil) extends Controller with SecureSocial with I18nSupport {

    val env: RuntimeEnvironment = environment

    val jsMessagesFactory = new JsMessagesFactory(messages)

    def index = UserAwareAction(implicit request => {
        implicit val userOpt: Option[BasicProfile] = request.user.asInstanceOf[Option[BasicProfile]]
        Ok(views.html.index())
    })

    def jsMessages(page: String) = Action {
        val jsMessages = if (page.nonEmpty && jsMessagesFactory.filtering(_.startsWith(page)).allMessages.nonEmpty)
            jsMessagesFactory.filtering(key => key.startsWith(page) || key.startsWith("error") || key.startsWith("format") || key.startsWith("constraint") || key.startsWith("global"))
        else
            jsMessagesFactory.all
        Ok(jsMessages.all(Some("window.Messages")))
    }

    override def messagesApi: MessagesApi = messages
}
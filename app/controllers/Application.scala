package controllers


import javax.inject.Inject

import common.ref.MtrConfigRef
import discord.DiscordBot
import jsmessages.JsMessagesFactory
import org.webjars.play.WebJarsUtil
import play.api.db.Database
import play.api.libs.ws.WSClient
import play.api.mvc._

class Application @Inject()(implicit cc: ControllerComponents, messagesAction: MessagesActionBuilder, db: Database, webJarsUtil: WebJarsUtil, ws: WSClient, discordBot: DiscordBot, config: MtrConfigRef) extends AbstractController(cc) {

    val jsMessagesFactory = new JsMessagesFactory(messagesApi)

    def index: Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        Ok(views.html.index())
    }

    def jsMessages(page: String): Action[AnyContent] = Action { implicit request =>
        val jsMessages = if (page.nonEmpty && jsMessagesFactory.filtering(_.startsWith(page)).allMessages.nonEmpty)
            jsMessagesFactory.filtering(key => key.startsWith(page) || key.startsWith("error") || key.startsWith("format") || key.startsWith("constraint") || key.startsWith("global"))
        else
            jsMessagesFactory.all
        Ok(jsMessages.all(Some("window.Messages")))
    }
}
package controllers

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

import com.cobble.bot.common.ref.MtrConfigRef
import discord.DiscordBot
import org.webjars.play.WebJarsUtil
import play.api.db.Database
import play.api.libs.ws.WSClient
import play.api.mvc._

class Auth @Inject()(implicit cc: ControllerComponents, messagesAction: MessagesActionBuilder, db: Database, webJarsUtil: WebJarsUtil, ws: WSClient, mtrConfigRef: MtrConfigRef, discordBot: DiscordBot) extends AbstractController(cc) {

    def twitchToken: Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val twitchOAuthRedirectUrl: String = "https://api.twitch.tv/kraken/oauth2/authorize?response_type=token" +
            "&client_id=" + mtrConfigRef.twitchClientId +
            "&scope=" + URLEncoder.encode(mtrConfigRef.twitchNeededOauthScopes.mkString(" "), StandardCharsets.UTF_8.name()) +
            "&redirect_uri=" + URLEncoder.encode(routes.Auth.twitchToken().absoluteURL(), StandardCharsets.UTF_8.name())
        if (request.queryString.contains("error"))
            Ok(views.html.auth.twitchToken(twitchOAuthRedirectUrl,
                request.queryString("error").headOption,
                request.queryString("error_description").headOption
            ))
        else
            Ok(views.html.auth.twitchToken(twitchOAuthRedirectUrl, None, None))
    }
}

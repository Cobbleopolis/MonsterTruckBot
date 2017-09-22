package controllers

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.DiscordApiUtil
import discord.DiscordBot
import org.webjars.play.WebJarsUtil
import play.api.db.Database
import play.api.libs.json.Reads._
import play.api.libs.ws.WSClient
import play.api.mvc._
import util.AuthUtil

import scala.concurrent.{ExecutionContext, Future}

class Auth @Inject()(implicit cc: ControllerComponents, messagesAction: MessagesActionBuilder, db: Database, webJarsUtil: WebJarsUtil, ws: WSClient, mtrConfigRef: MtrConfigRef, discordBot: DiscordBot, authUtil: AuthUtil, ec: ExecutionContext, discordApiUtil: DiscordApiUtil) extends AbstractController(cc) {

    def login: Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        Redirect(discordApiUtil.getOauthAuthUrl(routes.Auth.auth().absoluteURL()))
    }

    def auth: Action[AnyContent] = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
        discordApiUtil.getOauthAccessToken(request.getQueryString("code").getOrElse(""), routes.Auth.auth().absoluteURL()).flatMap { tokenResponse =>
            val accessToken: Option[String] = (tokenResponse.json \ "access_token").asOpt[String]
            val tokenType: Option[String] = (tokenResponse.json \ "token_type").asOpt[String]
            val expiresIn: Option[Long] = (tokenResponse.json \ "expires_in").asOpt[Long]
            val refreshToken: Option[String] = (tokenResponse.json \ "refresh_token").asOpt[String]
            val scope: Option[String] = (tokenResponse.json \ "scope").asOpt[String]
            if (accessToken.isDefined && tokenType.isDefined && expiresIn.isDefined && refreshToken.isDefined && scope.isDefined) {
                discordApiUtil.getUser(tokenType.get, accessToken.get).map { userResponse =>
                    val userId: Option[String] = (userResponse.json \ "id").asOpt[String]
                    val username: Option[String] = (userResponse.json \ "username").asOpt[String]
                    val discriminator: Option[String] = (userResponse.json \ "discriminator").asOpt[String]
                    if (userId.isDefined && username.isDefined && discriminator.isDefined) {
                        val fullUsername: String = username.get + "#" + discriminator.get
                        authUtil.setUserData(userId.get, accessToken.get, tokenType.get, expiresIn.get, refreshToken.get, scope.get, fullUsername)
                        Redirect(routes.Dashboard.dashboard()).withSession(request.session +
                            "userId" -> userId.get +
                            "username" -> fullUsername
                        )
                    } else
                        InternalServerError(request.messages("error.auth.login.error", userResponse.json))
                }
            } else
                Future.successful(InternalServerError(request.messages("error.auth.login.error", tokenResponse.json)))
        }
    }

    def logout: Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        Redirect(routes.Application.index()).withSession(request.session -
            "userId" -
            "username"
        )
    }

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

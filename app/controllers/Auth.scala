package controllers

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

import common.ref.MtrConfigRef
import common.util.DiscordApiUtil
import discord.DiscordBot
import org.webjars.play.WebJarsUtil
import play.api.Logger
import play.api.db.Database
import play.api.libs.json.Json
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
                        authUtil.setTokenData(userId.get, accessToken.get, tokenType.get, expiresIn.get, refreshToken.get, scope.get)
                        Ok(views.html.auth.setAuth(routes.Dashboard.dashboard().absoluteURL())).withSession(Session(request.session.data ++ Map(
                            "userId" -> userId.get,
                            "username" -> fullUsername
                        )))
                    } else
                        InternalServerError(views.html.auth.setAuth(routes.Dashboard.dashboard().absoluteURL(), Some(Json.prettyPrint(tokenResponse.json))))
                }
            } else
                Future.successful(InternalServerError(views.html.auth.setAuth(routes.Dashboard.dashboard().absoluteURL(), Some(Json.prettyPrint(tokenResponse.json)))))
        }
    }

    def logout: Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val userIdOpt: Option[String] = request.session.get("userId")
        if (userIdOpt.isDefined)
            authUtil.deleteUserData(userIdOpt.get)
        Redirect(routes.Application.index()).withNewSession
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

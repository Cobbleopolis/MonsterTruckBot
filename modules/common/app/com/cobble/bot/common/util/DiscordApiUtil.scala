package com.cobble.bot.common.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

import com.cobble.bot.common.ref.MtrConfigRef
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

class DiscordApiUtil @Inject()(implicit ws: WSClient, mtrConfigRef: MtrConfigRef, ec: ExecutionContext) {

    val BASE_URL: String = "https://discordapp.com/api"

    val CURRENT_USER: String = BASE_URL + "/users/@me"

    val CURRENT_USER_GUILDS: String = CURRENT_USER + "/guilds"

    def getOauthAuthUrl(redirectUri: String): String = {
        val params: Map[String, String] = Map(
            "response_type" -> "code",
            "client_id" -> mtrConfigRef.discordClientId,
            "scope" -> mtrConfigRef.oauthScopes.mkString(" "),
            "redirect_uri" -> redirectUri
        )
        mtrConfigRef.oauthAuthUrl + "?" + params.map(param => param._1 + "=" + URLEncoder.encode(param._2, StandardCharsets.UTF_8.name())).mkString("&")
    }

    def getOauthAccessToken(code: String, redirectUri: String): Future[WSResponse] = {
        val params: Map[String, String] = Map(
            "client_id" -> mtrConfigRef.discordClientId,
            "client_secret" -> mtrConfigRef.discordSecret,
            "grant_type" -> "authorization_code",
            "code" -> code,
            "redirect_uri" -> redirectUri
        )
        ws.url(mtrConfigRef.oauthTokenUrl).post(params)
    }

    def getOauthAccessTokenFromRefreshToken(refreshToken: String, redirectUri: String): Future[WSResponse] = {
        val params: Map[String, String] = Map(
            "client_id" -> mtrConfigRef.discordClientId,
            "client_secret" -> mtrConfigRef.discordSecret,
            "grant_type" -> "refresh_token",
            "refresh_token" -> refreshToken,
            "redirect_uri" -> redirectUri
        )
        ws.url(mtrConfigRef.oauthTokenUrl).post(params)
    }

    def getUser(tokenType: String, accessToken: String): Future[WSResponse] = {
        ws.url(CURRENT_USER).withHttpHeaders("Authorization" -> s"$tokenType $accessToken").get()
    }

}

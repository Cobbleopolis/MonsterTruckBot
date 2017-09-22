package util

import javax.inject.Inject

import play.api.cache.SyncCacheApi
import play.cache.NamedCache

import scala.concurrent.duration._

class AuthUtil @Inject()(@NamedCache("auth") authCache: SyncCacheApi) {

    val ACCESS_TOKEN_SUFFIX: String = "accessToken"

    val TOKEN_TYPE_SUFFIX: String = "tokenType"

    val EXPIRES_IN_SUFFIX: String = "expiresIn"

    val REFRESH_TOKEN_SUFFIX: String = "refreshToken"

    val SCOPE_SUFFIX: String = "scope"

    val USERNAME_SUFFIX: String = "username"

    private def getLocation(userId: String, suffix: String): String = userId + "." + suffix

    def setAccessToken(userId: String, accessToken: String, expiresIn: Duration): Unit =
        authCache.set(getLocation(userId, ACCESS_TOKEN_SUFFIX), accessToken, expiresIn)

    def getAccessToken(userId: String): Option[String] =
        authCache.get[String](getLocation(userId, ACCESS_TOKEN_SUFFIX))

    def setTokenType(userId: String, tokenType: String, expiresIn: Duration): Unit =
        authCache.set(getLocation(userId, TOKEN_TYPE_SUFFIX), tokenType, expiresIn)

    def getTokenType(userId: String): Option[String] =
        authCache.get[String](getLocation(userId, TOKEN_TYPE_SUFFIX))

    def setExpiresIn(userId: String, expiresInLong: Long, expiresIn: Duration): Unit =
        authCache.set(getLocation(userId, EXPIRES_IN_SUFFIX), expiresInLong, expiresIn)

    def getExpiresIn(userId: String): Option[Long] =
        authCache.get[Long](getLocation(userId, EXPIRES_IN_SUFFIX))

    def setRefreshToken(userId: String, refreshToken: String, expiresIn: Duration): Unit =
        authCache.set(getLocation(userId, REFRESH_TOKEN_SUFFIX), refreshToken, expiresIn)

    def getRefreshToken(userId: String): Option[String] =
        authCache.get[String](getLocation(userId, REFRESH_TOKEN_SUFFIX))

    def setScope(userId: String, scope: String, expiresIn: Duration): Unit =
        authCache.set(getLocation(userId, SCOPE_SUFFIX), scope, expiresIn)

    def getScope(userId: String): Option[String] =
        authCache.get[String](getLocation(userId, SCOPE_SUFFIX))

    def setUsername(userId: String, username: String, expiresIn: Duration): Unit =
        authCache.set(getLocation(userId, USERNAME_SUFFIX), username, expiresIn)

    def getUsername(userId: String): Option[String] =
        authCache.get[String](getLocation(userId, USERNAME_SUFFIX))

    def setUserData(userId: String, accessToken: String, tokenType: String, expiresIn: Long, refreshToken: String, scope: String, username: String): Unit = {
        val expiresDuration: Duration = expiresIn.seconds
        setAccessToken(userId, accessToken, expiresDuration)
        setTokenType(userId, tokenType, expiresDuration)
        setExpiresIn(userId, expiresIn, expiresDuration)
        setRefreshToken(userId, refreshToken, expiresDuration * 2)
        setScope(userId, scope, expiresDuration)
        setUsername(userId, username, expiresDuration)
    }
}

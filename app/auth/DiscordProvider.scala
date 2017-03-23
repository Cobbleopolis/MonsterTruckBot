package auth

import play.api.libs.ws.WSClient
import securesocial.core._
import securesocial.core.services.{CacheService, RoutesService}

import scala.concurrent.Future

class DiscordProvider(routesService: RoutesService, cacheService: CacheService, client: OAuth2Client, ws: WSClient) extends OAuth2Provider(routesService, client, cacheService) {
    override val id: String = DiscordProvider.Discord
    val GetAuthenticatedUser: String = "http://discordapp.com/api/users/@me"
    val UserId: String = "id"
    val Username: String = "username"
    val Discriminator: String = "discriminator"
    val Avatar: String = "avatar"

    override def fillProfile(info: OAuth2Info): Future[BasicProfile] = {
        ws.url(GetAuthenticatedUser).withHeaders("Authorization" -> s"${info.tokenType.get} ${info.accessToken}").get().map(res => {
            val user = res.json
            val userId: String = (user \ UserId).as[String]
            val username: Option[String] = (user \ Username).asOpt[String]
            val discriminator: Option[String] = (user \ Discriminator).asOpt[String]
            val avatarUrl: Option[String] = (user \ Avatar).asOpt[String] match {
                case Some(avatarHash) => Some(s"https://cdn.discordapp.com/avatars/$userId/$avatarHash.webp")
                case _ => None
            }
            val fullUsername: Option[String] = Some(username.getOrElse("") + "#" + discriminator.getOrElse(""))
            BasicProfile(id, userId, None, None, fullUsername, None, avatarUrl, authMethod, None, Some(info))
        })
    }

}

object DiscordProvider {
    val Discord: String = "discord"
}
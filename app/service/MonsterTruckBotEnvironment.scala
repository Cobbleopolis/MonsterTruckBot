package service

import javax.inject.{Inject, Singleton}

import auth.DiscordProvider
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.ws.WSClient
import securesocial.core.{IdentityProvider, RuntimeEnvironment}

import scala.collection.immutable.ListMap

@Singleton
class MonsterTruckBotEnvironment @Inject()(override val configuration: Configuration, override val messagesApi: MessagesApi, ws: WSClient) extends RuntimeEnvironment.Default {
    override type U = DemoUser

    override def userService: MonsterTruckBotUserService = new MonsterTruckBotUserService()

    override lazy val providers: ListMap[String, IdentityProvider] = ListMap(
        include(new DiscordProvider(routes, cacheService, oauth2ClientFor(DiscordProvider.Discord), ws))
    )
}

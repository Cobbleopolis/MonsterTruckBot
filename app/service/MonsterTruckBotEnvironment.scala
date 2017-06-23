package service

import javax.inject.{Inject, Singleton}

import auth.DiscordProvider
import controllers.WebJarAssets
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.ws.WSClient
import securesocial.controllers.ViewTemplates
import securesocial.core.{BasicProfile, IdentityProvider, RuntimeEnvironment}

import scala.collection.immutable.ListMap

@Singleton
class MonsterTruckBotEnvironment @Inject()(implicit override val configuration: Configuration, override val messagesApi: MessagesApi, ws: WSClient, mtrUserService: MonsterTruckBotUserService, webJarAssets: WebJarAssets) extends RuntimeEnvironment.Default {
    override type U = BasicProfile

    override lazy val viewTemplates: ViewTemplates = {
        new MonsterTruckBotAuthTemplates(this)(configuration, webJarAssets, messagesApi)
    }

    override lazy val providers: ListMap[String, IdentityProvider] = ListMap(
        include(new DiscordProvider(routes, cacheService, oauth2ClientFor(DiscordProvider.Discord), ws))
    )

    override def userService: MonsterTruckBotUserService = mtrUserService
}

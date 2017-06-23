package service

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.cache.CacheApi
import play.cache.NamedCache
import securesocial.core._
import securesocial.core.providers.MailToken
import securesocial.core.services.{SaveMode, UserService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.implicitConversions

@Singleton
class MonsterTruckBotUserService @Inject()(implicit conf: Configuration, @NamedCache("auth") authCache: CacheApi) extends UserService[BasicProfile] {

    def find(providerId: String, userId: String): Future[Option[BasicProfile]] = Future(authCache.get(s"auth.user.$userId").asInstanceOf[Option[BasicProfile]])

    def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = Future.successful(None)

    def save(profile: BasicProfile, mode: SaveMode): Future[BasicProfile] = Future {
        authCache.set(s"auth.user.${profile.userId}", profile, 2.hours)
        profile
    }

    def link(current: BasicProfile, to: BasicProfile): Future[BasicProfile] = Future.successful(current)

    def saveToken(token: MailToken): Future[MailToken] = Future.successful(token)

    def findToken(token: String): Future[Option[MailToken]] = Future.successful(None)

    def deleteToken(uuid: String): Future[Option[MailToken]] = Future.successful(None)

    def deleteExpiredTokens(): Unit = {}

    override def updatePasswordInfo(user: BasicProfile, info: PasswordInfo): Future[Option[BasicProfile]] = Future.successful(Some(user))

    override def passwordInfoFor(user: BasicProfile): Future[Option[PasswordInfo]] = Future.successful(None)
}
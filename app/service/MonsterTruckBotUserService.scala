/**
  * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
package service

import javax.inject.{Inject, Singleton}

import auth.DiscordProvider
import com.cobble.bot.common.models.User
import play.api.db.Database
import securesocial.core._
import securesocial.core.providers.MailToken
import securesocial.core.services.{SaveMode, UserService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class MonsterTruckBotUserService @Inject()(implicit db: Database) extends UserService[User] {

    implicit def userToBasicProfile(user: User): BasicProfile = BasicProfile(DiscordProvider.Discord, user.discordId, None, None, Some(user.username), None, Some(user.avatarUrl), AuthenticationMethod.OAuth2, None, Some(OAuth2Info(user.accessToken, Some(user.tokenType), None, Some(user.refreshToken))))

    implicit def basicProfileToUser(profile: BasicProfile): User = {
        val oAuth2Info: OAuth2Info = profile.oAuth2Info.get
        User(profile.userId, profile.fullName.get, profile.avatarUrl.get, oAuth2Info.accessToken, oAuth2Info.tokenType.get, oAuth2Info.refreshToken.get)
    }

    def find(providerId: String, userId: String): Future[Option[BasicProfile]] = Future(User.get(userId).asInstanceOf[Option[BasicProfile]])

    def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = Future.successful(None)

    def save(profile: BasicProfile, mode: SaveMode): Future[User] = Future {
        mode match {
            case SaveMode.SignUp =>
                User.insert(profile)
                profile
            case SaveMode.LoggedIn =>
                // first see if there is a user with this BasicProfile already.
                if (User.get(profile.userId).isDefined)
                    User.update(profile.userId, basicProfileToUser(profile).namedParameters: _*)
                else
                    User.insert(profile)
                profile
        }
    }

    def link(current: User, to: BasicProfile): Future[User] = Future.successful(current)

    def saveToken(token: MailToken): Future[MailToken] = Future.successful(token)

    def findToken(token: String): Future[Option[MailToken]] = Future.successful(None)

    def deleteToken(uuid: String): Future[Option[MailToken]] = Future.successful(None)

    def deleteExpiredTokens(): Unit = {}

    override def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = Future.successful(Some(user))

    override def passwordInfoFor(user: User): Future[Option[PasswordInfo]] = Future.successful(None)
}
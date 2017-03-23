package auth

import java.net.URLEncoder
import java.util.UUID

import play.api.libs.ws.WSClient
import play.api.mvc.{AnyContent, Request, Results}
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

    override def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = {
        val maybeError = request.queryString.get(OAuth2Constants.Error).flatMap(_.headOption).map {
            case OAuth2Constants.AccessDenied => Future.successful(AuthenticationResult.AccessDenied())
            case error =>
                Future.failed {
                    logger.error(s"[securesocial] error '$error' returned by the authorization server. Provider is $id")
                    AuthenticationException()
                }
        }
        maybeError.getOrElse {
            request.queryString.get(OAuth2Constants.Code).flatMap(_.headOption) match {
                case Some(code) =>
                    // we're being redirected back from the authorization server with the access code.
                    authenticateCallback(request, code)
                case None =>
                    // There's no code in the request, this is the first step in the oauth flow
                    val state = UUID.randomUUID().toString
                    val sessionId = request.session.get(IdentityProvider.SessionId).getOrElse(UUID.randomUUID().toString)
                    cacheService.set(sessionId, state, 300).map {
                        _ =>
                            var params = List(
                                (OAuth2Constants.ClientId, settings.clientId),
                                (OAuth2Constants.RedirectUri, routesService.authenticationUrl(id)),
                                (OAuth2Constants.ResponseType, OAuth2Constants.Code),
                                (OAuth2Constants.State, state)
                            )
                            settings.scope.foreach(s => {
                                params = (OAuth2Constants.Scope, s) :: params
                            })
                            settings.authorizationUrlParams.foreach(e => {
                                params = e :: params
                            })
                            val url = settings.authorizationUrl +
                                params.map(p => URLEncoder.encode(p._1, "UTF-8") + "=" + {
                                    if (p._1 == OAuth2Constants.Scope) p._2 else URLEncoder.encode(p._2, "UTF-8")
                                }).mkString("?", "&", "")
                            logger.debug("[securesocial] authorizationUrl = %s".format(settings.authorizationUrl))
                            logger.debug("[securesocial] redirecting to: [%s]".format(url))
                            AuthenticationResult.NavigationFlow(Results.Redirect(url).withSession(request.session + (IdentityProvider.SessionId -> sessionId)))
                    }
            }
        }
    }

    private[this] def authenticateCallback(request: Request[AnyContent], code: String): Future[AuthenticationResult] = {
        validateOauthState(request).flatMap(stateOk => if (stateOk) {
            for {
                accessToken <- getAccessToken(code)(request) if stateOk
                user <- fillProfile(OAuth2Info(accessToken.accessToken, accessToken.tokenType, accessToken.expiresIn, accessToken.refreshToken))
            } yield {
                logger.debug(s"[securesocial] user loggedin using provider $id = $user")
                AuthenticationResult.Authenticated(user)
            }
        } else {
            logger.warn("[securesocial] missing sid in session.")
            Future.successful(AuthenticationResult.AccessDenied())
        })
    }

    override def fillProfile(info: OAuth2Info): Future[BasicProfile] = {
        ws.url(GetAuthenticatedUser).withHeaders("Authorization" -> s"Bearer ${info.accessToken}").get().map(res => {
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

    private[this] def validateOauthState(request: Request[AnyContent]): Future[Boolean] = {
        val sessId: Option[String] = request.session.get(IdentityProvider.SessionId)
        val stateInQueryString: Option[String] = request.queryString.get(OAuth2Constants.State).flatMap(_.headOption)
        val cacheSessId: Option[Future[Option[String]]] = sessId.map(cacheService.getAs[String])
        cacheSessId.fold(Future.successful(false))(_.map(_ == stateInQueryString))
    }
}

object DiscordProvider {
    val Discord: String = "discord"
}
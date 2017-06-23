package util

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.util.DiscordApiUtil
import models.UserServersResponse
import models.UserServersResponse._
import play.api.cache.CacheApi
import play.api.libs.ws.WSClient
import play.cache.NamedCache
import securesocial.core.BasicProfile
import sx.blah.discord.handle.obj.Permissions

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthUtil @Inject()(implicit ws: WSClient, @NamedCache("auth") authCache: CacheApi, context: ExecutionContext) {

    def getManageableServers(user: BasicProfile, checkPermission: Permissions = Permissions.MANAGE_SERVER): Future[Seq[UserServersResponse]] = ws.url(DiscordApiUtil.CURRENT_USER_GUILDS).withHeaders("Authorization" -> s"${user.oAuth2Info.get.tokenType.get} ${user.oAuth2Info.get.accessToken}").get().map(res => {
        val serversOpt = res.json.asOpt[Seq[UserServersResponse]]
        if (serversOpt.isDefined) {
            val servers: Seq[UserServersResponse] = serversOpt.get.filter(s => s.owner || checkPermission.hasPermission(s.permissions)).sortBy(_.name)
            authCache.set(s"manageableServers.${user.userId}", servers)
            servers
        } else {
            val serversCacheOpt: Option[Seq[UserServersResponse]] = authCache.get[Seq[UserServersResponse]](s"manageableServers.${user.userId}")
            if (serversCacheOpt.isDefined)
                serversCacheOpt.get
            else
                Seq()
        }
    })

}

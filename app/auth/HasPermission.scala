package auth

import com.cobble.bot.common.ref.MtrConfigRef
import play.api.Configuration
import play.api.mvc.RequestHeader
import securesocial.core.{Authorization, BasicProfile}
import sx.blah.discord.handle.obj.Permissions
import util.AuthUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class HasPermission(permission: Permissions = Permissions.MANAGE_SERVER)(implicit authUtil: AuthUtil, config: MtrConfigRef) extends Authorization[BasicProfile] {

    override def isAuthorized(user: BasicProfile, request: RequestHeader): Boolean = {
        val hasPermission: Future[Boolean] = authUtil.getManageableServers(user, permission).map(_.map(_.id).contains(java.lang.Long.toUnsignedString(config.guildId)))
        Await.result(hasPermission, 300000.milliseconds)
    }

}

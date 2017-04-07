package auth

import com.cobble.bot.common.models.User
import play.api.mvc.RequestHeader
import securesocial.core.Authorization
import sx.blah.discord.handle.obj.Permissions
import util.AuthUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class HasPermission(guildId: String, permission: Permissions = Permissions.MANAGE_SERVER)(implicit authUtil: AuthUtil) extends Authorization[User] {

    override def isAuthorized(user: User, request: RequestHeader): Boolean = {
        val hasPermission: Future[Boolean] = authUtil.getManageableServers(user, permission).map(_.map(_.id).contains(guildId))
        Await.result(hasPermission, 300000.milliseconds)
    }

}

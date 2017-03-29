package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.{BotInstance, User}
import com.cobble.bot.common.util.DiscordApiUtil
import models.UserServersResponse
import models.UserServersResponse._
import play.api.db.Database
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import sx.blah.discord.handle.obj.Permissions

import scala.concurrent.duration._

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets, environment: RuntimeEnvironment, ws: WSClient) extends Controller with SecureSocial {

    val env: RuntimeEnvironment = environment

    def index = UserAwareAction { implicit request => {
        implicit val userOpt: Option[User] = request.user.asInstanceOf[Option[User]]
        Ok(views.html.index(BotInstance.getAll.map(_.toString)))
    }
    }

    def servers: Action[AnyContent] = SecuredAction.async { implicit request => {
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        ws.url(DiscordApiUtil.CURRENT_USER_GUILDS).withHeaders("Authorization" -> s"${userOpt.get.tokenType} ${userOpt.get.accessToken}").get().map( res => {
            val servers: Array[UserServersResponse] = res.json.as[List[UserServersResponse]].toArray.sortBy(_.name)
            Ok(views.html.servers(servers.filter(s => s.owner || Permissions.MANAGE_SERVER.hasPermission(s.permissions))))
        })
    }
    }

    def dashboard(id: String) = SecuredAction { implicit request => {
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        val botInstanceOpt: Option[BotInstance] = BotInstance.get(id)
        if (botInstanceOpt.isDefined)
            Ok(views.html.dashboard(botInstanceOpt.get))
        else
            NotFound(s"Bot instance $id not found")
    }
    }

}
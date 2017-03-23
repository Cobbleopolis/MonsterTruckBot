package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.BotInstance
import play.api.db.Database
import play.api.mvc._
import securesocial.core.{RuntimeEnvironment, SecureSocial}
import service.DemoUser

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets, environment: RuntimeEnvironment) extends Controller with SecureSocial {

    val env: RuntimeEnvironment = environment

    def index = UserAwareAction { implicit request => {
        implicit val userOpt: Option[DemoUser] = request.user.asInstanceOf[Option[DemoUser]]
        Ok(views.html.index(BotInstance.getAll.map(_.toString)))
    }
    }

    def dashboard = SecuredAction { implicit request => {
        implicit val userOpt: Option[DemoUser] = Some(request.user.asInstanceOf[DemoUser])
        Ok(views.html.index(List("Dash")))
    }
    }

}
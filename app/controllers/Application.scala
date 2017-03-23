package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.{BotInstance, User}
import play.api.db.Database
import play.api.mvc._
import securesocial.core.{RuntimeEnvironment, SecureSocial}

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets, environment: RuntimeEnvironment) extends Controller with SecureSocial {

    val env: RuntimeEnvironment = environment

    def index = UserAwareAction { implicit request => {
        implicit val userOpt: Option[User] = request.user.asInstanceOf[Option[User]]
        Ok(views.html.index(BotInstance.getAll.map(_.toString)))
    }
    }

    def dashboard = SecuredAction { implicit request => {
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        Ok(views.html.dashboard())
    }
    }

}
package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.BotInstance
import play.api.db.Database
import play.api.mvc._

class Application @Inject()(implicit db: Database, webJarAssets: WebJarAssets) extends Controller {

    def index = Action {
        Ok(views.html.index(BotInstance.getAll.map(_.toString)))
    }

}
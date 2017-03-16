package controllers


import javax.inject.Inject

import com.cobble.bot.common.models.BotInstance
import play.api.db.Database
import play.api.mvc._

class Application @Inject()(implicit db: Database) extends Controller {

    def index = Action {
        val botInstance: Option[BotInstance] = BotInstance.getAll.headOption
        if (botInstance.isDefined)
            Ok(views.html.index(botInstance.get.toString))
        else
            Ok(views.html.index("Your new application is ready."))
    }

}
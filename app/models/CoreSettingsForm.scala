package models

import com.cobble.bot.common.models.{BotInstance, CoreSettings}
import play.api.data._
import play.api.data.Forms._
import play.api.db.Database

case class CoreSettingsForm(twitchAccount: Option[String], moderatorRole: Option[String])

object CoreSettingsForm {

    val form: Form[CoreSettings] = Form(
        mapping(
            "guildId" -> longNumber,
            "moderatorRole" -> optional(text)
        )(CoreSettings.apply)(CoreSettings.unapply)
    )

}

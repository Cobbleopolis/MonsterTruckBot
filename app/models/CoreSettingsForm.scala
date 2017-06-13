package models

import com.cobble.bot.common.models.{BotInstance, CoreSettings}
import play.api.data._
import play.api.data.Forms._
import play.api.db.Database

case class CoreSettingsForm(twitchAccount: Option[String], moderatorRole: Option[String])

object CoreSettingsForm {

    def get(guildId: String)(implicit db: Database): Option[CoreSettingsForm] = {
        val botInstanceOpt: Option[BotInstance] = BotInstance.get(guildId)
        val coreSettings: Option[CoreSettings] = CoreSettings.get(guildId)
        if (botInstanceOpt.isDefined && coreSettings.isDefined)
            Some(CoreSettingsForm(botInstanceOpt.get.twitchAccount, coreSettings.get.moderatorRoleId))
        else
            None
    }

    val form = Form(
        mapping(
            "twitchAccount" -> optional(text),
            "moderatorRole" -> optional(text)
        )(apply)(unapply)
    )

}

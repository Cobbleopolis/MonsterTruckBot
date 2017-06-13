package models

import com.cobble.bot.common.models.FilterSettings
import play.api.data.Form
import play.api.data.Forms._

case class FilterSettingsForm()

object FilterSettingsForm {

    def form(guildId: String): Form[FilterSettings] = Form(
        mapping(
            "guildId" -> text,
            "capsFilterEnabled" -> boolean,
            "capsFilterThreshold" -> number(min = 1, max = 100)
        )(FilterSettings.apply)(FilterSettings.unapply)
    )

}

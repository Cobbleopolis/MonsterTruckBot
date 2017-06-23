package models

import com.cobble.bot.common.models.FilterSettings
import play.api.data.Form
import play.api.data.Forms._

case class FilterSettingsForm()

object FilterSettingsForm {

    def form: Form[FilterSettings] = Form(
        mapping(
            "guildId" -> longNumber,
            "capsFilterEnabled" -> boolean,
            "capsFilterThreshold" -> number(min = 1, max = 100)
        )(FilterSettings.apply)(FilterSettings.unapply)
    )

}

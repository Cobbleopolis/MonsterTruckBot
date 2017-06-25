package models

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.FilterSettings
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.data.Form
import play.api.data.Forms._

@Singleton
class DashboardSettingsForms @Inject()(mtrConfigRef: MtrConfigRef) {

    val filterForm: Form[FilterSettings] = Form(
        mapping(
            "guildId" -> ignored(mtrConfigRef.guildId),
            "capsFilterEnabled" -> boolean,
            "capsFilterThreshold" -> number(min = 1, max = 100),
            "linksFilterEnabled" -> boolean
        )(FilterSettings.apply)(FilterSettings.unapply)
    )

}

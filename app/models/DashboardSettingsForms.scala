package models

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.data.{Form, FormError}
import play.api.data.Forms._

@Singleton
class DashboardSettingsForms @Inject()(mtrConfigRef: MtrConfigRef) {

    val filterForm: Form[FilterSettings] = Form(
        mapping(
            "guildId" -> ignored(mtrConfigRef.guildId),
            "capsFilterEnabled" -> boolean,
            "capsFilterExemptionLevel" -> number,
            "capsFilterThreshold" -> number(min = 1, max = 100),
            "linksFilterEnabled" -> boolean,
            "linksFilterExemptionLevel" -> number,
            "blacklistFilterEnabled" -> boolean,
            "blacklistFilterExemptionLevel" -> number,
            "blacklistFilterWords" -> text
        )(FilterSettings.apply)(FilterSettings.unapply)
    )

    val newCommandForm: Form[CustomCommand] = Form(
        mapping(
            "guildId" -> ignored(mtrConfigRef.guildId),
            "commandName" -> text,
            "permissionLevel" -> number,
            "commandContent" -> text
        )(CustomCommand.apply)(CustomCommand.unapply)
    )

    val existingCommandFormError: FormError = FormError("commandName", "dashboard.forms.customCommands.newCommand.errors.commandExists")

}

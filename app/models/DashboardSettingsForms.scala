package models

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.{BitTrackingFormData, CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.{BitTrackingRef, MessageRef, MtrConfigRef}
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

    val commandForm: Form[CustomCommand] = Form(
        mapping(
            "guildId" -> ignored(mtrConfigRef.guildId),
            "commandName" -> nonEmptyText,
            "permissionLevel" -> number,
            "commandContent" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE)
        )(CustomCommand.apply)(CustomCommand.unapply)
    )

    val deleteCommandForm = Form(
        single(
            "commandName" -> nonEmptyText
        )
    )

    val bitTrackingForm: Form[BitTrackingFormData] = Form(
        mapping(
            "totalBits" -> number(min = 0),
            "goalAmount" -> number(min = 0),
            "toNextGoal" -> number(min = 0),
            "goalCount" -> number(min = 0)
        )(BitTrackingFormData.apply)(BitTrackingFormData.unapply)
    )

    val existingCommandFormError: FormError = FormError("commandName", "dashboard.forms.customCommands.newCommand.errors.commandExists")

}

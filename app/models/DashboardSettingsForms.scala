package models

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.{BitTrackingFormData, CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.{MessageRef, MtrConfigRef}
import play.api.data.Forms._
import play.api.data.{Form, FormError}

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
            "guildId" -> ignored(mtrConfigRef.guildId),
            "currentMode" -> number,
            //Nip Dip
            "nipDipTemplate" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
            "nipDipGoalAmount" -> number(min = 0),
            "nipDipToNextGoal" -> number(min = 0),
            "nipDipGoalCount" -> number(min = 0),
            //RBG
            //Jackshots
            "jackshotsTemplate" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
            "jackshotsGoalAmount" -> number(min = 0),
            "jackshotsToNextGoal" -> number(min = 0),
            "jackshotsGoalCount" -> number(min = 0)
        )(BitTrackingFormData.apply)(BitTrackingFormData.unapply)
    )

    val existingCommandFormError: FormError = FormError("commandName", "dashboard.forms.customCommands.newCommand.errors.commandExists")

}

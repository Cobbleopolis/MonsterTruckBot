package models

import javax.inject.{Inject, Singleton}

import com.cobble.bot.common.models.bitTrackingFormData._
import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.{MessageRef, MtrConfigRef}
import play.api.data.Forms._
import play.api.data.{Form, FormError}

@Singleton
class DashboardSettingsForms @Inject()(mtrConfigRef: MtrConfigRef) {
    
    val MAX_INT_INPUT_VALUE: Int = Int.MaxValue - 1

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
            "common" -> mapping(
                "paused" -> boolean
            )(CommonBitTrackingFormData.apply)(CommonBitTrackingFormData.unapply),
            "nipDip" -> mapping(
                "template" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
                "goalAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "toNextGoal" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "goalCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE)
            )(CollectiveModeFormData.apply)(CollectiveModeFormData.unapply),
            "rbg" -> mapping(
                "template" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
                "greenShotAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "greenShotCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "blueShotAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "blueShotCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "redShotAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "redShotCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE)
            )(RBGModeFormData.apply)(RBGModeFormData.unapply),
            "jackshots" -> mapping(
                "template" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
                "goalAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "toNextGoal" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "goalCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE)
            )(CollectiveModeFormData.apply)(CollectiveModeFormData.unapply),
            "pushUp" -> mapping(
                "template" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
                "cheerMode" -> nonEmptyText,
                "goalAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "toNextGoal" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "pushUpSetAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "goalCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE)
            )(PushUpModeFormData.apply)(PushUpModeFormData.unapply),
            "singItOrSlamIt" -> mapping(
                "template" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
                "goalAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "goalCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "roundsWon" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
                "roundsLost" -> number(min = 0, max = MAX_INT_INPUT_VALUE)
            )(SingItOrSlamItModeFormData.apply)(SingItOrSlamItModeFormData.unapply)
        )(BitTrackingFormData.apply)(BitTrackingFormData.unapply)
    )

    val existingCommandFormError: FormError = FormError("commandName", "dashboard.forms.customCommands.newCommand.errors.commandExists")

}

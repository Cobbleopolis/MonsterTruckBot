package models

import javax.inject.{Inject, Singleton}

import common.models.bitTrackingFormData._
import common.models.{CustomCommand, FilterSettings, TwitchRegular}
import common.ref.{MessageRef, MtrConfigRef}
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

    val bitTrackingForm: Form[BitTrackingFormData] = Form(
        mapping(
            "bitTrackingMode" -> nonEmptyText,
            "isPaused" -> boolean,
            "gameMessage" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
            "bitsMessage" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
            "goalMessage" -> nonEmptyText(maxLength = MessageRef.TWITCH_MAX_MESSAGE_LENGTH_USABLE),
            "toNextGoal" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
            "goalAmount" -> number(min = 0, max = MAX_INT_INPUT_VALUE),
            "goalCount" -> number(min = 0, max = MAX_INT_INPUT_VALUE)
        )(BitTrackingFormData.apply)(BitTrackingFormData.unapply)
    )

    val existingCommandFormError: FormError = FormError("commandName", "dashboard.customCommands.newCommand.errors.commandExists")

    val twitchRegularForm: Form[TwitchRegular] = Form(
        mapping(
            "guildId" -> ignored(mtrConfigRef.guildId),
            "twitchUsername" -> nonEmptyText(minLength = 4, maxLength = 25)
        )(TwitchRegular.apply)(TwitchRegular.unapply)
    )

    val existingTwitchRegularFormError: FormError = FormError("twitchUsername", "dashboard.twitchRegulars.newTwitchRegular.errors.regularExists")

}

package discord.commands

import javax.inject.Inject

import common.api.commands.BitsCommand
import common.models.BitTrackingSettings
import common.ref.MtrConfigRef
import common.util.BitTrackingUtil
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database

class DiscordBitsCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef, bitTrackingUtil: BitTrackingUtil) extends DiscordCommand with BitsCommand {

    override def execute(implicit executionEvent: DiscordCommandExecutionEvent): Unit = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined) {
            messageUtil.replyNoAt(getFormattedResponse(bitTrackingSettingsOpt.get, bitTrackingUtil))
        } else
            messageUtil.reply("bot.bitTracking.settingsNotFound")
    }

}

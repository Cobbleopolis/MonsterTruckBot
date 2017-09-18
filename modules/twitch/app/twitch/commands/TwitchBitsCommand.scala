package twitch.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.BitsCommand
import com.cobble.bot.common.models.BitTrackingSettings
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.BitTrackingUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import twitch.api.TwitchCommand
import twitch.events.TwitchCommandExecutionEvent
import twitch.util.TwitchMessageUtil

class TwitchBitsCommand @Inject()(implicit val messageUtil: TwitchMessageUtil, db: Database, cache: SyncCacheApi, mtrConfigRef: MtrConfigRef, bitTrackingUtil: BitTrackingUtil) extends TwitchCommand with BitsCommand {

    override def execute(implicit executionEvent: TwitchCommandExecutionEvent): Unit = {
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined) {
            messageUtil.reply(getFormattedResponse(bitTrackingSettingsOpt.get, bitTrackingUtil))
        } else
            messageUtil.reply("bot.bitTracking.settingsNotFound")
    }

}

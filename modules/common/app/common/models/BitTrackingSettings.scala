package common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser}
import common.api.bitTracking.BitTrackingMode
import common.api.bitTracking.BitTrackingMode.BitTrackingMode
import common.api.{Model, ModelAccessor}

case class BitTrackingSettings(guildId: Long, currentMode: Int = 0, bitGameMessage: String = "", bitsMessage: String = "", goalMessage: String = "") extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'guild_id -> guildId,
        'current_mode -> currentMode,
        'bit_game_message -> bitGameMessage,
        'bits_message -> bitsMessage,
        'goal_message -> goalMessage
    )

    def getCurrentMode: BitTrackingMode = BitTrackingMode(currentMode)

}

object BitTrackingSettings extends ModelAccessor[BitTrackingSettings, Long] {

    override val tableName = "bit_tracking_settings"

    override val idSymbol = 'guild_id

    override val insertQuery = s"INSERT INTO $tableName (guild_id, current_mode, bit_game_message, bits_message, goal_message) VALUES ({guild_id}, {current_mode}, {bit_game_message}, {bits_message}, {goal_message})"

    override val parser: RowParser[BitTrackingSettings] = Macro.parser[BitTrackingSettings]("guild_id", "current_mode", "bit_game_message", "bits_message", "goal_message")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
}

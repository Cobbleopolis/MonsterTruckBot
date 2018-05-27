package common.models

import anorm.{Macro, NamedParameter, RowParser, SqlParser}
import common.api.bitTracking.BitTrackingGame
import common.api.bitTracking.BitTrackingGame.BitTrackingGame
import common.api.{Model, ModelAccessor}

case class BitTrackingSettings(guildId: Long, currentMode: Int = 0, nipDipTemplate: String = "", rbgTemplate: String = "", jackshotsTemplate: String = "", pushUpTemplate: String = "", singItOrSlamItTemplate: String = "") extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'guild_id -> guildId,
        'current_mode -> currentMode,
        'nip_dip_template -> nipDipTemplate,
        'rbg_template -> rbgTemplate,
        'jackshots_template -> jackshotsTemplate,
        'push_up_template -> pushUpTemplate,
        'sing_it_or_slam_it_template -> singItOrSlamItTemplate
    )

    def getCurrentMode: BitTrackingGame = BitTrackingGame(currentMode)

}

object BitTrackingSettings extends ModelAccessor[BitTrackingSettings, Long] {

    override val tableName = "bit_tracking_settings"

    override val idSymbol = 'guild_id

    override val insertQuery = s"INSERT INTO $tableName (guild_id, current_mode, nip_dip_template, rbg_template, jackshots_template, push_up_template, sing_it_or_slam_it_template) VALUES ({guild_id}, {current_mode}, {nip_dip_template}, {rbg_template}, {jackshots_template}, {push_up_template}, {sing_it_or_slam_it_template})"

    override val parser: RowParser[BitTrackingSettings] = Macro.parser[BitTrackingSettings]("guild_id", "current_mode", "nip_dip_template", "rbg_template", "jackshots_template", "push_up_template", "sing_it_or_slam_it_template")

    override val insertParser: RowParser[Long] = SqlParser.scalar[Long]
}

package data

import common.components.DaoComponents
import common.models.{BitTrackingSettings, BotInstance, FilterSettings}
import common.ref.MtrConfigRef
import javax.inject.{Inject, Singleton}

@Singleton
class DatabaseCreation @Inject()(daoComponents: DaoComponents, mtrConfigRef: MtrConfigRef) {

    val botInstanceOpt: Option[BotInstance] = daoComponents.botInstanceDAO.get(mtrConfigRef.guildId)
    if (botInstanceOpt.isEmpty)
        daoComponents.botInstanceDAO.insert(mtrConfigRef.guildId, BotInstance(mtrConfigRef.guildId))

    val filterSettingsOpt: Option[FilterSettings] = daoComponents.filterSettingsDAO.get(mtrConfigRef.guildId)
    if (filterSettingsOpt.isEmpty)
        daoComponents.filterSettingsDAO.insert(mtrConfigRef.guildId, FilterSettings(mtrConfigRef.guildId))

    val bitTrackingSettingsOpt: Option[BitTrackingSettings] = daoComponents.bitTrackingSettingsDAO.get(mtrConfigRef.guildId)
    if (bitTrackingSettingsOpt.isEmpty)
        daoComponents.bitTrackingSettingsDAO.insert(mtrConfigRef.guildId, BitTrackingSettings(mtrConfigRef.guildId))
}

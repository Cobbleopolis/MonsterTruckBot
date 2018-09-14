package common.components

import common.dao._
import javax.inject.Inject

class DaoComponents @Inject()(
                                 val bitTrackingSettingsDAO: BitTrackingSettingsDAO,
                                 val botInstanceDAO: BotInstanceDAO,
                                 val customCommandDAO: CustomCommandDAO,
                                 val filterSettingsDAO: FilterSettingsDAO,
                                 val twitchRegularDAO: TwitchRegularDAO
                             )

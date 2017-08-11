package com.cobble.bot.common.models

import com.cobble.bot.common.ref.BitTrackingRef

case class BitTrackingFormData(
                                  guildId: Long,
                                  currentMode: Int,
                                  //Nip Dip
                                  nipDipTemplate: String = "",
                                  nipDipGoalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                                  nipDipToNextGoal: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL,
                                  nipDipGoalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT,
                                  //RBG
                                  //Jackshots
                                  jackshotsTemplate: String = "",
                                  jackshotsGoalAmount: Int = BitTrackingRef.DEFAULT_GOAL_AMOUNT,
                                  jackshotsToNextGoal: Int = BitTrackingRef.DEFAULT_TO_NEXT_GOAL,
                                  jackshotsGoalCount: Int = BitTrackingRef.DEFAULT_GOAL_COUNT
                              ) {
    def getBitTrackingSettings: BitTrackingSettings = BitTrackingSettings(
        guildId = guildId,
        currentMode = currentMode,
        nipDipTemplate = nipDipTemplate,
        jackshotsTemplate = jackshotsTemplate
    )
}

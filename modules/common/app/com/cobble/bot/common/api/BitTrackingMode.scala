package com.cobble.bot.common.api

import com.cobble.bot.common.api

object BitTrackingMode extends Enumeration {

    type BitTrackingMode = Value

    val NONE = Value(0)
    val NIP_DIP = Value(1)
    val RBG = Value(2)
    val JACKSHOTS = Value(3)
    val PUSH_UP = Value(4)
    val SING_IT_OR_SLAM_IT = Value(5)

    val map: Map[String, String] = values.map(p => {p.id.toString -> s"dashboard.forms.bitTracking.modes.${p.toString.toLowerCase()}"}).toSeq.sortBy(_._1).toMap

}

package com.cobble.bot.common.api

object PermissionLevel extends Enumeration {
    type PermissionLevel = Value
    val EVERYONE = Value(0)
    val SUBSCRIBERS = Value(1)
    val MODERATORS = Value(2)
    val OWNER = Value(3)

    val map: Map[String, String] = values.map(p => p.id.toString -> s"global.permissionLevels.${p.toString.toLowerCase}").toMap
}

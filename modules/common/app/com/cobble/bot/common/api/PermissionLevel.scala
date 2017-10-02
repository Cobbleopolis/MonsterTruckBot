package com.cobble.bot.common.api

import scala.collection.SortedMap

object PermissionLevel extends Enumeration {
    type PermissionLevel = Value
    val EVERYONE: PermissionLevel = Value(0)
    val SUBSCRIBERS: PermissionLevel = Value(1)
    val REGULARS: PermissionLevel = Value(2)
    val MODERATORS: PermissionLevel = Value(3)
    val OWNER: PermissionLevel = Value(4)

    val map: SortedMap[String, String] = SortedMap(values.map(p =>
        p.id.toString -> s"global.permissionLevels.${p.toString.toLowerCase}"
    ).toSeq: _*)
}

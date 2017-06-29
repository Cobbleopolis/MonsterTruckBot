package com.cobble.bot.common.api

import com.cobble.bot.common.api.PermissionLevel.PermissionLevel

trait Command {

    /** The name of the command. This is used to call it. */
    val name: String

    /** Dictates if the command can only be called by mods. */
    val permissionLevel: PermissionLevel

}

package common.api

import common.api.PermissionLevel.PermissionLevel

trait Command {

    lazy val description: String = s"bot.help.descriptions.$name"
    /** The name of the command. This is used to call it. */
    val name: String
    /** Dictates if the command can only be called by mods. */
    val permissionLevel: PermissionLevel

}

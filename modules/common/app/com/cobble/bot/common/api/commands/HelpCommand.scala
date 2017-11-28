package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.{Command, PermissionLevel}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel

trait HelpCommand extends Command {

    override val name: String = "help"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

}

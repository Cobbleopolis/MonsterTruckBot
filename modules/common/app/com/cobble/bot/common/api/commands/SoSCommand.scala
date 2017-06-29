package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.{Command, PermissionLevel}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel

trait SoSCommand extends Command {

    override val name: String = "sos"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

}

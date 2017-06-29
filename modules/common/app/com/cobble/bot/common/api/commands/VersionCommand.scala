package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.{Command, PermissionLevel}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel

trait VersionCommand extends Command {

    override val name: String = "version"

    override val permissionLevel: PermissionLevel = PermissionLevel.SUBSCRIBERS

}

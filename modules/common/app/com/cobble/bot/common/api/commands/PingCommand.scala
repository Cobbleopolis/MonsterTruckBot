package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.{Command, PermissionLevel}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel

trait PingCommand extends Command {

    override val name: String = "ping"

    override val permissionLevel: PermissionLevel = PermissionLevel.SUBSCRIBERS

}

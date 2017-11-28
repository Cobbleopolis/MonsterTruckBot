package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.{Command, PermissionLevel}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel

trait BitTestCommand extends Command {

    override val name: String = "bittest"

    override val permissionLevel: PermissionLevel = PermissionLevel.MODERATORS

}

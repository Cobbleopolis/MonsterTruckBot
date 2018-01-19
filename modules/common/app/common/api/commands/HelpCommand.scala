package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.{Command, PermissionLevel}

trait HelpCommand extends Command {

    override val name: String = "help"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

}

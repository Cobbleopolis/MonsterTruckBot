package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.{Command, PermissionLevel}

trait BitTestCommand extends Command {

    override val name: String = "bittest"

    override val permissionLevel: PermissionLevel = PermissionLevel.MODERATORS

}

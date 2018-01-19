package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.{Command, PermissionLevel}

trait SoSCommand extends Command {

    override val name: String = "sos"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

}

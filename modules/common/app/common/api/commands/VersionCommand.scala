package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.{Command, PermissionLevel}

trait VersionCommand extends Command {

    override val name: String = "version"

    override val permissionLevel: PermissionLevel = PermissionLevel.SUBSCRIBERS

}

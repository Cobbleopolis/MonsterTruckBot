package common.api.commands

import common.api.PermissionLevel.PermissionLevel
import common.api.{Command, PermissionLevel}

trait PingCommand extends Command {

    override val name: String = "ping"

    override val permissionLevel: PermissionLevel = PermissionLevel.SUBSCRIBERS

}

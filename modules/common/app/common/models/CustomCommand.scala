package common.models

import anorm.NamedParameter
import common.api.PermissionLevel.PermissionLevel
import common.api.{Model, PermissionLevel}

case class CustomCommand(guildId: Long, commandName: String, permissionLevel: Int = PermissionLevel.EVERYONE.id, commandContent: String = "") extends Model {

    override val namedParameters: Seq[NamedParameter] = Seq(
        'guild_id -> guildId,
        'command_name -> commandName,
        'permission_level -> permissionLevel,
        'command_content -> commandContent
    )

    val getPermissionLevel: PermissionLevel = PermissionLevel(permissionLevel)
}


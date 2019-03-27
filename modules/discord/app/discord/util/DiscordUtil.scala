package discord.util

import common.api.PermissionLevel
import common.api.PermissionLevel.PermissionLevel
import discord.DiscordBot
import discord4j.core.`object`.entity.{Member, User}
import javax.inject.{Inject, Provider}
import reactor.core.scala.publisher._
import collection.JavaConverters._

class DiscordUtil @Inject()(discordBot: Provider[DiscordBot]) {

    def getUserPermissionLevel(user: User): PermissionLevel = {
        val memberOpt: Option[Member] = user.asMember(discordBot.get.guildSnowflake).blockOptional()
        if (memberOpt.isDefined) {
            val userRoleIds = memberOpt.get.getRoleIds.asScala
            if (discordBot.get().guild.isDefined && user.getId == discordBot.get().guild.get.getOwnerId)
                PermissionLevel.OWNER
            else if (userRoleIds.contains(discordBot.get().moderatorRoleSnowflake))
                PermissionLevel.MODERATORS
            else if (userRoleIds.contains(discordBot.get().regularRoleSnowflake))
                PermissionLevel.REGULARS
            else if (userRoleIds.contains(discordBot.get().subscriberRoleSnowflake))
                PermissionLevel.SUBSCRIBERS
            else
                PermissionLevel.EVERYONE
        } else
            PermissionLevel.EVERYONE
    }

}

package com.cobble.bot.common.api.commands

import javax.naming.ConfigurationException

import com.cobble.bot.common.api.{Command, PermissionLevel, TwitchChannelInfo}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.util.TwitchApiUtil

import scala.concurrent.{ExecutionContext, Future}

trait SubCountCommand extends Command {

    override val name: String = "count"

    override val permissionLevel: PermissionLevel = PermissionLevel.MODERATORS

    def getTotalSubCount(twitchChannelInfo: Option[TwitchChannelInfo], twitchApiUtil: TwitchApiUtil)(implicit executionContext: ExecutionContext): Future[Int] = {
        if (twitchChannelInfo.isDefined)
            twitchApiUtil.channelSubscriptionsRequest(twitchChannelInfo.get).get().map(response => (response.json \ "_total").as[Int])
        else
            Future.failed(new ConfigurationException("Twitch channel configuration not found for the calling channel"))
    }

}

package com.cobble.bot.common.api.commands

import javax.naming.ConfigurationException

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.{Command, PermissionLevel, TwitchChannelInfo}
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.util.TwitchApiUtil
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

trait SubCountCommand extends Command with DefaultLang {

    override val name: String = "count"

    override val permissionLevel: PermissionLevel = PermissionLevel.MODERATORS

    def getTotalSubCount(twitchChannelInfo: Option[TwitchChannelInfo], twitchApiUtil: TwitchApiUtil)(implicit executionContext: ExecutionContext, messagesApi: MessagesApi): Future[Int] = {
        if (twitchChannelInfo.isDefined)
            if (twitchChannelInfo.get.oauth.isDefined)
                if (twitchChannelInfo.get.channelId.isDefined)
                    twitchApiUtil.channelSubscriptionsRequest(twitchChannelInfo.get).get().map(response => (response.json \ "_total").as[Int])
                else
                    Future.failed(new ConfigurationException(messagesApi("error.twitch.missing.channelId")))
            else
                Future.failed(new ConfigurationException(messagesApi("error.twitch.missing.oauth")))
        else
            Future.failed(new ConfigurationException(messagesApi("error.twitch.channelDoesNotExist")))
    }

}

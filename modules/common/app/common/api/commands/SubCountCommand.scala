package common.api.commands

import javax.naming.ConfigurationException

import common.DefaultLang
import common.api.{Command, PermissionLevel, TwitchChannelInfo}
import common.api.PermissionLevel.PermissionLevel
import common.util.TwitchApiUtil
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

trait SubCountCommand extends Command with DefaultLang {

    override val name: String = "count"

    override val permissionLevel: PermissionLevel = PermissionLevel.MODERATORS

    def getTotalSubCount(twitchChannelInfo: Option[TwitchChannelInfo], twitchApiUtil: TwitchApiUtil)(implicit executionContext: ExecutionContext, messagesApi: MessagesApi): Future[Int] = {
        if (twitchChannelInfo.isDefined)
            if (twitchChannelInfo.get.oauth.isDefined)
                if (twitchChannelInfo.get.channelId.isDefined)
                    twitchApiUtil.getChannelSubscriptionsRequest(twitchChannelInfo.get).get().map(response => (response.json \ "_total").as[Int] - 1) // Subtract 1 because Twitch counts the twitch channel user as a sub.
                else
                    Future.failed(new ConfigurationException(messagesApi("error.twitch.missing.channelId")))
            else
                Future.failed(new ConfigurationException(messagesApi("error.twitch.missing.oauth")))
        else
            Future.failed(new ConfigurationException(messagesApi("error.twitch.channelDoesNotExist")))
    }

}

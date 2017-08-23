package com.cobble.bot.common.api.commands

import javax.naming.ConfigurationException

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.api.{Command, PermissionLevel, TwitchChannelInfo}
import com.cobble.bot.common.util.TwitchApiUtil
import play.api.i18n.MessagesApi
import play.api.libs.json.JsResult.Exception
import play.api.libs.json.{JsUndefined, JsValue}

import scala.concurrent.{ExecutionContext, Future}

trait UptimeCommand extends Command with DefaultLang {

    override val name: String = "uptime"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

    def getStreamUptime(twitchChannelInfo: Option[TwitchChannelInfo], twitchApiUtil: TwitchApiUtil)(implicit executionContext: ExecutionContext, messagesApi: MessagesApi): Future[String] = {
        if (twitchChannelInfo.isDefined)
            if (twitchChannelInfo.get.channelId.isDefined)
                twitchApiUtil.getChannelStreamRequest(twitchChannelInfo.get).get().map(response => {
                    if ((response.json \ "stream" \ "created_at").isDefined)
                        // TODO Get difference
                        messagesApi("bot.uptime.message", (response.json \ "stream" \ "created_at").as[String])
                    else if ((response.json \ "stream").isDefined)
                        messagesApi("bot.uptime.streamOffline")
                    else
                        throw new java.lang.Exception(messagesApi("bot.commandExecutionError", response.json))
                })
            else
                Future.failed(new ConfigurationException(messagesApi("error.twitch.missing.channelId")))
        else
            Future.failed(new ConfigurationException(messagesApi("error.twitch.channelDoesNotExist")))
    }

}

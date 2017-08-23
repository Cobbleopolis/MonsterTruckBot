package com.cobble.bot.common.api.commands


import javax.naming.ConfigurationException

import com.cobble.bot.common.DefaultLang
import com.cobble.bot.common.api.PermissionLevel.PermissionLevel
import com.cobble.bot.common.api.{Command, PermissionLevel, TwitchChannelInfo}
import com.cobble.bot.common.util.TwitchApiUtil
import org.joda.time.{DateTime, Duration, Instant, Period}
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

trait UptimeCommand extends Command with DefaultLang {

    override val name: String = "uptime"

    override val permissionLevel: PermissionLevel = PermissionLevel.EVERYONE

    def getStreamUptime(twitchChannelInfo: Option[TwitchChannelInfo], twitchApiUtil: TwitchApiUtil)(implicit executionContext: ExecutionContext, messagesApi: MessagesApi): Future[String] = {
        if (twitchChannelInfo.isDefined)
            if (twitchChannelInfo.get.channelId.isDefined)
                twitchApiUtil.getChannelStreamRequest(twitchChannelInfo.get).get().map(response => {
                    if ((response.json \ "stream" \ "created_at").isDefined) {
                        val streamStart: DateTime = Instant.parse((response.json \ "stream" \ "created_at").as[String]).toDateTime
                        val period: Period = new Period(new Duration(Math.abs(DateTime.now().getMillis - streamStart.getMillis)).getMillis)
                        val formattedPeriod: String = getFormattedPeriod(period)
                        if (formattedPeriod.nonEmpty)
                            messagesApi("bot.uptime.message", formattedPeriod)
                        else
                            messagesApi("bot.uptime.streamJustWentLive")
                    } else if ((response.json \ "stream").isDefined)
                        messagesApi("bot.uptime.streamOffline")
                    else
                        throw new java.lang.Exception(messagesApi("bot.commandExecutionError", response.json))
                })
            else
                Future.failed(new ConfigurationException(messagesApi("error.twitch.missing.channelId")))
        else
            Future.failed(new ConfigurationException(messagesApi("error.twitch.channelDoesNotExist")))
    }

    private def getFormattedPeriod(period: Period)(implicit messagesApi: MessagesApi): String = {
        var out: String = ""

        if (period.getYears > 0)
            if (period.getYears == 1)
                out += s"${period.getYears} ${messagesApi("global.timeUnits.year.singular")} "
            else
                out += s"${period.getYears} ${messagesApi("global.timeUnits.year.plural")} "

        if (period.getMonths > 0)
            if (period.getMonths == 1)
                out += s"${period.getMonths} ${messagesApi("global.timeUnits.month.singular")} "
            else
                out += s"${period.getMonths} ${messagesApi("global.timeUnits.month.plural")} "

        if (period.getDays > 0)
            if (period.getDays == 1)
                out += s"${period.getDays} ${messagesApi("global.timeUnits.day.singular")} "
            else
                out += s"${period.getDays} ${messagesApi("global.timeUnits.day.plural")} "

        if (period.getHours > 0)
            if (period.getHours == 1)
                out += s"${period.getHours} ${messagesApi("global.timeUnits.hour.singular")} "
            else
                out += s"${period.getHours} ${messagesApi("global.timeUnits.hour.plural")} "

        if (period.getMinutes > 0)
            if (period.getMinutes == 1)
                out += s"${period.getMinutes} ${messagesApi("global.timeUnits.minute.singular")} "
            else
                out += s"${period.getMinutes} ${messagesApi("global.timeUnits.minute.plural")} "

        if (period.getSeconds > 0)
            if (period.getSeconds == 1)
                out += s"${period.getSeconds} ${messagesApi("global.timeUnits.second.singular")}"
            else
                out += s"${period.getSeconds} ${messagesApi("global.timeUnits.second.plural")}"

        out.trim
    }

}

package common.api.commands


import javax.naming.ConfigurationException

import common.DefaultLang
import common.api.PermissionLevel.PermissionLevel
import common.api.{Command, PermissionLevel, TwitchChannelInfo}
import common.util.TwitchApiUtil
import org.joda.time.{DateTime, Duration, Instant, Period}
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

trait UptimeCommand extends Command with DefaultLang {

    override val name: String = "streamuptime"

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
            out += s"${period.getYears} ${messagesApi("global.timeUnits.year", period.getYears)} "

        if (period.getMonths > 0)
            out += s"${period.getMonths} ${messagesApi("global.timeUnits.month", period.getMonths)} "

        if (period.getDays > 0)
            out += s"${period.getDays} ${messagesApi("global.timeUnits.day", period.getDays)} "

        if (period.getHours > 0)
            out += s"${period.getHours} ${messagesApi("global.timeUnits.hour", period.getHours)} "

        if (period.getMinutes > 0)
            out += s"${period.getMinutes} ${messagesApi("global.timeUnits.minute", period.getMinutes)} "

        if (period.getSeconds > 0)
            out += s"${period.getSeconds} ${messagesApi("global.timeUnits.second", period.getSeconds)}"

        out.trim
    }

}

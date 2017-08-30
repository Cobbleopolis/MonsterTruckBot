package twitch.events

import java.util
import java.util.Optional

import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, ServerMessage}
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent
import twitch.api.TwitchEvent
import twitch.api.usernotice.UserNoticeMessageId.UserNoticeMessageId
import twitch.api.usernotice.UserNoticeSubPlan.UserNoticeSubPlan
import twitch.api.usernotice.{UserNoticeMessageId, UserNoticeSubPlan}

class TwitchSubEvent(userNoticeEvent: UserNoticeEvent) extends TwitchEvent {

    def getUserNoticeEvent: UserNoticeEvent = userNoticeEvent

    val subPlan: UserNoticeSubPlan = UserNoticeSubPlan.withName(getTag("msg-param-sub-plan").get().getValue.orElse("Unknown"))

    val isPrime: Boolean = subPlan == UserNoticeSubPlan.PRIME

    val msgId: UserNoticeMessageId = UserNoticeMessageId.withName(getTag("msg-id").get().getValue.orElse("Unknown"))

    val isResub: Boolean = msgId == UserNoticeMessageId.RESUBSCRIPTION

    val resubMonthCount: Option[Int] = {
        val monthsOpt: Optional[String] = getTag("msg-param-months").get().getValue
        if (monthsOpt.isPresent)
            Some(monthsOpt.get().toInt)
        else
            None
    }

    val displayName: String = getTag("display-name").get().getValue.orElse(getTag("login").get().getValue.orElse(""))

    override def getClient: Client = userNoticeEvent.getClient

    override def getChannel: Channel = userNoticeEvent.getChannel

    override def getMessage: String = userNoticeEvent.getMessage.orElse("")

    override def getOriginalMessages: util.List[ServerMessage] = userNoticeEvent.getOriginalMessages
}

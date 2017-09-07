package twitch.events

import java.util
import java.util.Optional

import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, MessageTag, ServerMessage}
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent
import twitch.api.TwitchEvent
import twitch.api.usernotice.UserNoticeMessageId.UserNoticeMessageId
import twitch.api.usernotice.UserNoticeSubPlan.UserNoticeSubPlan
import twitch.api.usernotice.{UserNoticeMessageId, UserNoticeSubPlan}

class TwitchSubEvent(userNoticeEvent: UserNoticeEvent) extends TwitchEvent {

    def getUserNoticeEvent: UserNoticeEvent = userNoticeEvent

    val subPlan: UserNoticeSubPlan = {
        val tag: Optional[MessageTag] = getTag("msg-param-sub-plan")
        if (tag.isPresent && tag.get().getValue.isPresent)
            UserNoticeSubPlan.withName(tag.get().getValue.orElse("Unknown"))
        else
            UserNoticeSubPlan.UNKNOWN
    }

    val isPrime: Boolean = subPlan == UserNoticeSubPlan.PRIME

    val msgId: UserNoticeMessageId = {
        val msgIdTag: Optional[MessageTag] = getTag("msg-id")
        if (msgIdTag.isPresent && msgIdTag.get().getValue.isPresent)
            UserNoticeMessageId.withName(msgIdTag.get().getValue.get)
        else
            UserNoticeMessageId.UNKNOWN
    }

    val isResub: Boolean = msgId == UserNoticeMessageId.RESUBSCRIPTION

    val resubMonthCount: Option[Int] = {
        val paramMontsTag: Optional[MessageTag] = getTag("msg-param-months")
        if (paramMontsTag.isPresent && paramMontsTag.get().getValue.isPresent)
            Some(paramMontsTag.get.getValue.get().toInt)
        else
            None
    }

    val displayName: String = {
        val displayNameTag: Optional[MessageTag] = getTag("display-name")
        val loginTag: Optional[MessageTag] = getTag("login")
        if (displayNameTag.isPresent && displayNameTag.get().getValue.isPresent)
            displayNameTag.get().getValue.get()
        else if (loginTag.isPresent && loginTag.get().getValue.isPresent)
            loginTag.get().getValue.get()
        else
            ""
    }

    override def getClient: Client = userNoticeEvent.getClient

    override def getChannel: Channel = userNoticeEvent.getChannel

    override def getMessage: String = userNoticeEvent.getMessage.orElse("")

    override def getOriginalMessages: util.List[ServerMessage] = userNoticeEvent.getOriginalMessages
}

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

    val recipientDisplayName: String = {
        val paramRecipientDisplayName: Optional[MessageTag] = getTag("msg-param-recipient-display-name")
        val paramRecipientUserName: Optional[MessageTag] = getTag("msg-param-recipient-user-name")
        if (paramRecipientDisplayName.isPresent && paramRecipientDisplayName.get().getValue.isPresent)
            paramRecipientDisplayName.get.getValue.get()
        else if (paramRecipientUserName.isPresent && paramRecipientUserName.get().getValue.isPresent)
            paramRecipientUserName.get.getValue.get()
        else
            ""
    }

    val isMysteryGiftedSub: Boolean = msgId == UserNoticeMessageId.MYSTERY_GIFTED_SUBSCRIPTION

    val mysteryGiftedSubCount: Option[Int] = {
        val paramGiftCountTagOpt: Optional[MessageTag] = getTag("msg-param-mass-gift-count")
        if (paramGiftCountTagOpt.isPresent && paramGiftCountTagOpt.get().getValue.isPresent)
            Some(paramGiftCountTagOpt.get.getValue.get().toInt)
        else
            None
    }

    val mysteryGiftedSubSenderCount: Option[Int] = {
        val paramSenderCountTagOpt: Optional[MessageTag] = getTag("msg-param-sender-count")
        if (paramSenderCountTagOpt.isPresent && paramSenderCountTagOpt.get().getValue.isPresent)
            Some(paramSenderCountTagOpt.get.getValue.get().toInt)
        else
            None
    }

    val isGiftPaidUpgrade: Boolean = msgId == UserNoticeMessageId.GIFT_PAID_UPGRADE

    val senderName: String = {
        val paramSenderNameOpt: Optional[MessageTag] = getTag("msg-param-sender-name")
        val paramSenderLoginOpt: Optional[MessageTag] = getTag("msg-param-sender-login")
        if (paramSenderNameOpt.isPresent && paramSenderNameOpt.get().getValue.isPresent)
            paramSenderNameOpt.get.getValue.get()
        else if (paramSenderLoginOpt.isPresent && paramSenderLoginOpt.get().getValue.isPresent)
            paramSenderLoginOpt.get.getValue.get()
        else
            ""
    }

    override def getClient: Client = userNoticeEvent.getClient

    override def getChannel: Channel = userNoticeEvent.getChannel

    override def getMessage: String = userNoticeEvent.getMessage.orElse("")

    override def getOriginalMessages: util.List[ServerMessage] = userNoticeEvent.getOriginalMessages
}

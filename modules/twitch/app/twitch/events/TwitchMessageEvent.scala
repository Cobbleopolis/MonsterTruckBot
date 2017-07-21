package twitch.events

import java.util

import com.cobble.bot.common.util.MessageUtil
import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, ServerMessage, User}
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.feature.twitch.event.TwitchSingleMessageEvent

class TwitchMessageEvent(messageEvent: ChannelMessageEvent) extends TwitchSingleMessageEvent {

    def getClient: Client = messageEvent.getClient

    override def getOriginalMessages: util.List[ServerMessage] = messageEvent.getOriginalMessages

    def getActor: User = messageEvent.getActor

    def getChannel: Channel = messageEvent.getChannel

    def getMessage: String = messageEvent.getMessage

    def sendReply(msg: String): Unit = messageEvent.sendReply(s"@${getTag("display-name").get().getValue.get()} ${MessageUtil.arrowChar} $msg")

}

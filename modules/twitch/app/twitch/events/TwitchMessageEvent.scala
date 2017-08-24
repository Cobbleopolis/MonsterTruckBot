package twitch.events

import java.util

import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, ServerMessage, User}
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import twitch.api.TwitchChatMessageEvent

class TwitchMessageEvent(messageEvent: ChannelMessageEvent) extends TwitchChatMessageEvent {

    override def getMessageEvent: ChannelMessageEvent = messageEvent

    override def getClient: Client = messageEvent.getClient

    override def getOriginalMessages: util.List[ServerMessage] = messageEvent.getOriginalMessages

    override def getActor: User = messageEvent.getActor

    override def getChannel: Channel = messageEvent.getChannel

    override def getMessage: String = messageEvent.getMessage

}

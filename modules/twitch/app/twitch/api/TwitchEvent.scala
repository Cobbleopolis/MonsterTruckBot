package twitch.api

import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, User}
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.feature.twitch.event.TwitchSingleMessageEvent

trait TwitchEvent extends TwitchSingleMessageEvent {

    def getMessageEvent: ChannelMessageEvent

    def getClient: Client

    def getActor: User

    def getChannel: Channel

    def getMessage: String

    val displayName: String = getTag("display-name").get().getValue.orElse(getActor.getNick)

    val channelName: String = getChannel.getName.substring(1)

    val isMod: Boolean = getTag("mod").get().getValue.get().equals("1")

    val isSub: Boolean = getTag("subscriber").get().getValue.get().equals("1")

    def timeoutUser(reason: String, duration: Int = 5): Unit = getChannel.sendMessage(s"/timeout ${getActor.getNick} $duration $reason")

}

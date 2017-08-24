package twitch.api

import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, User}
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.feature.twitch.event.TwitchSingleMessageEvent

trait TwitchEvent extends TwitchSingleMessageEvent {

    def getClient: Client

    def getChannel: Channel

    def getMessage: String

    val channelName: String = getChannel.getName.substring(1)

    val isMod: Boolean = getTag("mod").get().getValue.get().equals("1")

    val isSub: Boolean = getTag("subscriber").get().getValue.get().equals("1")

}

package twitch.api

import org.kitteh.irc.client.library.element.User
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent

trait TwitchChatMessageEvent extends TwitchEvent {

    def getMessageEvent: ChannelMessageEvent

    def getActor: User

    val nick: String = getActor.getNick

    val displayName: String = getTag("display-name").get().getValue.orElse(nick)

    def timeoutUser(reason: String, duration: Int = 5): Unit = getChannel.sendMessage(s"/timeout $displayName $duration $reason")

}

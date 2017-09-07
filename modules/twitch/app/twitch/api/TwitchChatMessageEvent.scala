package twitch.api

import java.util.Optional

import org.kitteh.irc.client.library.element.{MessageTag, User}
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent

trait TwitchChatMessageEvent extends TwitchEvent {

    def getMessageEvent: ChannelMessageEvent

    def getActor: User

    val nick: String = getActor.getNick

    val displayName: String = {
        val displayNameTag: Optional[MessageTag] = getTag("display-name")
        if (displayNameTag.isPresent && displayNameTag.get.getValue.isPresent)
            displayNameTag.get().getValue.get()
        else
            nick
    }

    def timeoutUser(reason: String, duration: Int = 5): Unit = getChannel.sendMessage(s"/timeout $displayName $duration $reason")

}

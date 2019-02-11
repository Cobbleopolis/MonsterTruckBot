package twitch.api

import java.util.Optional

import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.{Channel, MessageTag}
import org.kitteh.irc.client.library.feature.twitch.event.TwitchSingleMessageEvent

trait TwitchEvent extends TwitchSingleMessageEvent {

    def getClient: Client

    def getChannel: Channel

    def getMessage: String

    val badges: Array[TwitchBadge] = {
        val messageTagOpt: Optional[MessageTag] = getTag("badges")
        if (messageTagOpt.isPresent) {
            val tagValue: String = messageTagOpt.get().getValue.orElse("")
            tagValue.split(',').map(s => {
                val sArr: Array[String] = s.split('/')
                TwitchBadge(sArr.head, sArr.last)
            })
        } else
            Array()
    }

    def hasBadge(badgeName: String): Boolean = badges.map(_.name).contains(badgeName)

    val channelName: String = getChannel.getName.substring(1)

    val isMod: Boolean = getTag("mod").get().getValue.get().equals("1")

    val isSub: Boolean = hasBadge("subscriber")

}

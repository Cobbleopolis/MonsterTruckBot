package common.util

import javax.inject.Inject

import common.api.TwitchChannelInfo
import common.ref.MtrConfigRef
import play.api.libs.ws.{WSClient, WSRequest}

class TwitchApiUtil @Inject()(ws: WSClient, mtrConfigRef: MtrConfigRef) {

    val baseTwitchApiURL: String = "https://api.twitch.tv/kraken"

    def getChannelSubscriptionsRequest(twitchChannelInfo: TwitchChannelInfo): WSRequest =
        baseWSRequest(getChannelSubscriptionsUrl(twitchChannelInfo.channelId.get), twitchChannelInfo)

    def baseWSRequest(url: String, twitchChannelInfo: TwitchChannelInfo, needsOAuth: Boolean = true): WSRequest = {
        val request = ws.url(url)
        if (twitchChannelInfo.oauth.isDefined && needsOAuth)
            request.withHttpHeaders(
                "Accept" -> "application/vnd.twitchtv.v5+json",
                "Client-ID" -> mtrConfigRef.twitchClientId,
                "Authorization" -> s"OAuth ${twitchChannelInfo.oauth.get}"
            )
        else
            request.withHttpHeaders(
                "Accept" -> "application/vnd.twitchtv.v5+json",
                "Client-ID" -> mtrConfigRef.twitchClientId
            )
    }

    def getChannelSubscriptionsUrl(channelId: Int): String = s"$baseTwitchApiURL/channels/$channelId/subscriptions"

    def getChannelStreamRequest(twitchChannelInfo: TwitchChannelInfo): WSRequest =
        baseWSRequest(getChannelStreamUrl(twitchChannelInfo.channelId.get), twitchChannelInfo, needsOAuth = false)

    def getChannelStreamUrl(channelId: Int): String = s"$baseTwitchApiURL/streams/$channelId"
}

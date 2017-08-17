package com.cobble.bot.common.util

import javax.inject.Inject

import com.cobble.bot.common.api.TwitchChannelInfo
import com.cobble.bot.common.ref.MtrConfigRef
import play.api.libs.ws.{WSClient, WSRequest}

class TwitchApiUtil @Inject()(ws: WSClient, mtrConfigRef: MtrConfigRef) {

    val baseTwitchApiURL: String = "https://api.twitch.tv/kraken"

    def baseWSRequest(url: String, twitchChannelInfo: TwitchChannelInfo): WSRequest = ws.url(url)
        .withHttpHeaders(
            "Accept" -> "application/vnd.twitchtv.v5+json",
            "Client-ID" -> mtrConfigRef.twitchClientId,
            "Authorization" -> s"OAuth ${twitchChannelInfo.oauth.get}"
        )

    def channelSubscriptionsRequest(twitchChannelInfo: TwitchChannelInfo): WSRequest =
        baseWSRequest(channelSubscriptionsUrl(twitchChannelInfo.channelId.get), twitchChannelInfo)

    def channelSubscriptionsUrl(channelId: Int): String = s"$baseTwitchApiURL/channels/$channelId/subscriptions"

}

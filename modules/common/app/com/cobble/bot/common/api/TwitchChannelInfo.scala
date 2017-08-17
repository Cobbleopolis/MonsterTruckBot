package com.cobble.bot.common.api

case class TwitchChannelInfo(name: String, channelId: Option[Int], oauth: Option[String]) {

    val properTwitchUsername: String = name.toLowerCase

    val ircChannelName: String = "#" + properTwitchUsername

}

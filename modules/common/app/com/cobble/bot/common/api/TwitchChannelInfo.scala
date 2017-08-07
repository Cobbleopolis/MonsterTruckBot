package com.cobble.bot.common.api

case class TwitchChannelInfo(name: String, oauth: String, displayName: String) {

    val ircChannelName: String = "#" + name

}

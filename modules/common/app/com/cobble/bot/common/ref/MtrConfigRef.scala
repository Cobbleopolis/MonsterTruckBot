package com.cobble.bot.common.ref

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.duration._

@Singleton
class MtrConfigRef @Inject()(implicit conf: Configuration) {

    val guildId: Long = java.lang.Long.parseUnsignedLong(conf.get[String]("mtrBot.guildId"))

    val moderatorRoleId: Long = java.lang.Long.parseUnsignedLong(conf.get[String]("mtrBot.moderatorRoleId"))

    val subscriberRoleId: Long = java.lang.Long.parseUnsignedLong(conf.get[String]("mtrBot.subscriberRoleId"))

    val commandPrefix: String = conf.get[String]("mtrBot.commandPrefix")

    val cacheTimeout: Duration = conf.getMillis("mtrBot.cacheTimeout").milliseconds

    val discordUsername: String = conf.get[String]("mtrBot.discord.username")

    val discordGame: String = conf.get[String]("mtrBot.discord.game")

    val discordClientId: String = conf.get[String]("mtrBot.discord.clientId")

    val discordToken: String = conf.get[String]("mtrBot.discord.token")

    val twitchUsername: String = conf.get[String]("mtrBot.twitch.username")

    val twitchOauth: String = conf.get[String]("mtrBot.twitch.oauth")

}

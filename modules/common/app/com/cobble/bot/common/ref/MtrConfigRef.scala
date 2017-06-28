package com.cobble.bot.common.ref

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.duration._

@Singleton
class MtrConfigRef @Inject()(implicit conf: Configuration) {

    val guildId: Long = java.lang.Long.parseUnsignedLong(conf.getString("mtrBot.guildId").get)

    val moderatorRoleId: Long = java.lang.Long.parseUnsignedLong(conf.getString("mtrBot.moderatorRoleId").get)

    val commandPrefix: String = conf.getString("mtrBot.commandPrefix").getOrElse("!")

    val cacheTimeout: Duration = conf.getMilliseconds("mtrBot.cacheTimeout").getOrElse(7200000L).milliseconds

    val discordUsername: String = conf.getString("mtrBot.discord.username").getOrElse("Monster Truck Bot")

    val discordGame: String = conf.getString("mtrBot.discord.game").getOrElse("Hap! Hap! Hap!")

    val discordClientId: Option[String] = conf.getString("mtrBot.discord.clientId")

    val discordToken: Option[String] = conf.getString("mtrBot.discord.token")

    val twitchUsername: Option[String] = conf.getString("mtrBot.twitch.username")

    val twitchOauth: Option[String] = conf.getString("mtrBot.twitch.oauth")

}

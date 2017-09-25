package com.cobble.bot.common.ref

import com.cobble.bot.common.api.TwitchChannelInfo
import com.google.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.duration._

@Singleton
class MtrConfigRef @Inject()(implicit conf: Configuration) {

    val guildId: Long = java.lang.Long.parseUnsignedLong(conf.get[String]("mtrBot.guildId"))

    val moderatorRoleId: Long = java.lang.Long.parseUnsignedLong(conf.get[String]("mtrBot.moderatorRoleId"))

    val subscriberRoleId: Long = java.lang.Long.parseUnsignedLong(conf.get[String]("mtrBot.subscriberRoleId"))

    val maintainerUserId: Option[Long] = conf.getOptional[String]("mtrBot.maintainerUserId").map(java.lang.Long.parseUnsignedLong)

    val commandPrefix: String = conf.get[String]("mtrBot.commandPrefix")

    val helpLink: String = conf.get[String]("mtrBot.helpLink")

    val cacheTimeout: Duration = conf.getMillis("mtrBot.cacheTimeout").milliseconds

    val discordUsername: String = conf.get[String]("mtrBot.discord.username")

    val discordGame: String = conf.get[String]("mtrBot.discord.game")

    val discordClientId: String = conf.get[String]("mtrBot.discord.clientId")

    val discordSecret: String = conf.get[String]("mtrBot.discord.secret")

    val discordToken: String = conf.get[String]("mtrBot.discord.token")

    val twitchUsername: String = conf.get[String]("mtrBot.twitch.username")

    val twitchOauth: String = conf.get[String]("mtrBot.twitch.oauth")

    val twitchClientId: String = conf.get[String]("mtrBot.twitch.clientId")

    val twitchNeededOauthScopes: Seq[String] = conf.get[Seq[String]]("mtrBot.twitch.neededOauthScopes")

    val twitchChannels: Map[String, TwitchChannelInfo] = conf.get[Seq[Configuration]]("mtrBot.twitch.channels").map(channel =>
        channel.get[String]("name").toLowerCase -> TwitchChannelInfo(
            channel.get[String]("name"),
            channel.getOptional[Int]("channelId"),
            channel.getOptional[String]("oauth"),
        )
    ).toMap

    val oauthAuthUrl: String = conf.get[String]("oauth.authUrl")

    val oauthTokenUrl: String = conf.get[String]("oauth.tokenUrl")

    val oauthScopes: Seq[String] = conf.get[Seq[String]]("oauth.scopes")

}

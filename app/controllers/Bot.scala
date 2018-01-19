package controllers

import javax.inject.Inject

import common.api.PermissionLevel
import common.models.CustomCommand
import common.ref.MtrConfigRef
import discord.{DiscordBot, DiscordCommandRegistry}
import org.webjars.play.WebJarsUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.mvc._
import twitch.TwitchCommandRegistry

import scala.collection.SortedMap
import scala.concurrent.{ExecutionContext, Future}

class Bot @Inject()(implicit cc: ControllerComponents, messagesAction: MessagesActionBuilder, webJarsUtil: WebJarsUtil, discordBot: DiscordBot, discordCommandRegistry: DiscordCommandRegistry, twitchCommandRegistry: TwitchCommandRegistry, database: Database, mtrConfigRef: MtrConfigRef, syncCacheApi: SyncCacheApi, ec: ExecutionContext) extends AbstractController(cc) {

    def botCommands: Action[AnyContent] = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
        Future {
            val discordMap: Map[String, (String, String)] = discordCommandRegistry.commands.map(command => command._1 -> (
                command._2.description,
                PermissionLevel.map.getOrElse(command._2.permissionLevel.id.toString, "global.notAvailable")
            ))
            val twitchMap: Map[String, (String, String)] = twitchCommandRegistry.commands.map(command => command._1 -> (
                command._2.description,
                PermissionLevel.map.getOrElse(command._2.permissionLevel.id.toString, "global.notAvailable")
            ))
            Ok(views.html.botCommands(SortedMap((discordMap ++ twitchMap).toArray: _*), discordCommandRegistry, twitchCommandRegistry, CustomCommand.getByGuildId(mtrConfigRef.guildId)))
        }
    }

}

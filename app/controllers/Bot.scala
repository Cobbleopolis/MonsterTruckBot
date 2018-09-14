package controllers

import common.api.PermissionLevel
import common.components.DaoComponents
import common.models.CustomCommand
import common.ref.MtrConfigRef
import discord.DiscordCommandRegistry
import javax.inject.Inject
import play.api.mvc._
import twitch.TwitchCommandRegistry

import scala.collection.SortedMap
import scala.concurrent.{ExecutionContext, Future}

class Bot @Inject()(
                       implicit cc: ControllerComponents,
                       daoComponents: DaoComponents,
                       messagesAction: MessagesActionBuilder,
                       configRef: MtrConfigRef,
                       discordCommandRegistry: DiscordCommandRegistry,
                       twitchCommandRegistry: TwitchCommandRegistry,
                       ec: ExecutionContext,
                       botCommandsTemplate: views.html.botCommands
                   ) extends AbstractController(cc) {

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
            Ok(botCommandsTemplate(SortedMap((discordMap ++ twitchMap).toArray: _*), daoComponents.customCommandDAO.getByGuildId(configRef.guildId)))
        }
    }

}

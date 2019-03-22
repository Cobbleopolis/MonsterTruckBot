package discord.controllers

import javax.inject.Inject

import discord.DiscordBot
import play.api.mvc._

class Application @Inject()(controllerComponents: ControllerComponents, discordBot: DiscordBot) extends AbstractController(controllerComponents) {

    def alive: Action[AnyContent] = Action {
        if (discordBot.botClient.isConnected)
            Ok("Hap! Hap! Hap!")
        else
            ServiceUnavailable("Not ready")
    }

}

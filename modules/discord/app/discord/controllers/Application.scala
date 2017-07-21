package discord.controllers

import javax.inject.Inject

import discord.DiscordBot
import play.api.mvc._

class Application @Inject()(controllerComponents: ControllerComponents, discordBot: DiscordBot) extends AbstractController(controllerComponents) {

    def alive = Action {
        if (discordBot.client.isReady)
            Ok("Hap! Hap! Hap!")
        else
            Ok("Not ready")
    }

}

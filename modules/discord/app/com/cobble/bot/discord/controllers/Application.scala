package com.cobble.bot.discord.controllers

import javax.inject.Inject

import com.cobble.bot.discord.DiscordBot
import play.api.mvc.{Action, Controller}

class Application @Inject()(discordBot: DiscordBot) extends Controller {

    def alive = Action {
        if (discordBot.client.isReady)
            Ok("Hap! Hap! Hap!")
        else
            Ok("Not ready")
    }

}

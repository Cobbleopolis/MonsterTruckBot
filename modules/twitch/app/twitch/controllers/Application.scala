package twitch.controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}
import twitch.TwitchBot

class Application @Inject()(controllerComponents: ControllerComponents, twitchBot: TwitchBot) extends AbstractController(controllerComponents) {

    def alive = Action {
        if (twitchBot.isConnected)
            Ok("Hap! Hap! Hap!")
        else
            ServiceUnavailable("Not ready")
    }

}

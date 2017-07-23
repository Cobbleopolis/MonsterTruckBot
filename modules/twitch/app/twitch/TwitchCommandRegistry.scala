package twitch

import javax.inject.{Singleton, Inject}

import twitch.api.TwitchCommand
import twitch.commands.TwitchPingCommand

@Singleton
class TwitchCommandRegistry @Inject()(
                                        val pingCommand: TwitchPingCommand
                                     ) {

    val commands: Map[String, TwitchCommand] = Map(
        pingCommand.name -> pingCommand
    )

}

package discord.event

import sx.blah.discord.api.events.Event
import sx.blah.discord.handle.obj.{IMessage, IUser}

class DiscordCommandExecutionEvent(message: IMessage, command: String, args: Array[String], user: IUser) extends Event {

    def getMessage: IMessage = message

    def getCommand: String = command

    def getArgs: Array[String] = args

    def getUser: IUser = user

    def isCommand(commandName: String): Boolean = command.equalsIgnoreCase(command)

}

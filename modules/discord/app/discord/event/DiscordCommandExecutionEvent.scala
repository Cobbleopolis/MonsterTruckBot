package discord.event

import ackcord.data.{Message, User}

class DiscordCommandExecutionEvent(message: Message, command: String, args: Array[String], user: User) {

    def getMessage: Message = message

    def getCommand: String = command.toLowerCase

    def getArgs: Array[String] = args

    def getUser: User = user

    def isCommand(commandName: String): Boolean = command.equalsIgnoreCase(command)

}

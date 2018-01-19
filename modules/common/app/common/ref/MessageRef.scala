package common.ref

object MessageRef {

    val TWITCH_MAX_MESSAGE_LENGTH: Int = 500

    val TWITCH_MAX_MESSAGE_LENGTH_USABLE: Int = TWITCH_MAX_MESSAGE_LENGTH - 50 //Subtract 50 because of the beginning section of the twitch message

    val DISCORD_MAX_MESSAGE_LENGTH: Int = 2000

}

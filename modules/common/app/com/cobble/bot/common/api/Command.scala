package com.cobble.bot.common.api

trait Command {

    /** The name of the command. This is used to call it. */
    val name: String

    /** Dictates if the command can only be called by mods. */
    val modOnly: Boolean

}

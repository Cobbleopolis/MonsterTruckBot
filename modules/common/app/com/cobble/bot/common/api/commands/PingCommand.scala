package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.Command

trait PingCommand extends Command {

    val name: String = "ping"

    val modOnly: Boolean = false

}

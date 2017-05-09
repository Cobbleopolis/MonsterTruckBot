package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.Command

trait SoSCommand extends Command {

    val name: String = "sos"

    val modOnly: Boolean = false

}

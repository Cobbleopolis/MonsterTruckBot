package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.Command

trait HelpCommand extends Command {

    override val name: String = "mthelp"

    override val modOnly: Boolean = false

}

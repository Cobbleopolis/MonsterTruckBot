package com.cobble.bot.common.api.commands

import com.cobble.bot.common.api.Command

trait VersionCommand extends Command {

    val name: String = "version"

    val modOnly: Boolean = false

}

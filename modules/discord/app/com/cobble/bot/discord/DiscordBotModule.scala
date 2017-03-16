package com.cobble.bot.discord

import com.google.inject.AbstractModule

class DiscordBotModule extends AbstractModule {

    override def configure(): Unit = {
        bind(classOf[DiscordBot]).asEagerSingleton()
    }

}

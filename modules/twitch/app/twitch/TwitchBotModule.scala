package twitch

import com.google.inject.AbstractModule

class TwitchBotModule extends AbstractModule {

    override def configure(): Unit = {
        bind(classOf[TwitchBot]).asEagerSingleton()
    }

}

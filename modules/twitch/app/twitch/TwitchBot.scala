package twitch

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.db.Database
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext

@Singleton
class TwitchBot @Inject()(implicit configuration: Configuration, lifecycle: ApplicationLifecycle, ex: ExecutionContext, db: Database) {
    TwitchLogger.info("Twitch bot has not been implemented yet")
}

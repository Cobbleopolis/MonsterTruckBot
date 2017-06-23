package modules

import javax.inject.Inject

import com.google.inject.AbstractModule
import data.DatabaseCreation
import play.api.db.Database
import play.api.inject._
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}
import securesocial.core.RuntimeEnvironment
import service.MonsterTruckBotEnvironment

class MonsterTruckBotModule extends AbstractModule with AkkaGuiceSupport {

    override def configure(): Unit = {
        bind(classOf[RuntimeEnvironment]).to(classOf[MonsterTruckBotEnvironment])
        bind(classOf[DatabaseCreation]).asEagerSingleton()
    }

//    override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
//
//    )

}

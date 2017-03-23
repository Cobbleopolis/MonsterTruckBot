package modules

import play.api.inject._
import play.api.{Configuration, Environment}
import securesocial.core.RuntimeEnvironment
import service.MonsterTruckBotEnvironment

class MonsterTruckBotModule extends Module {

    override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
        bind[RuntimeEnvironment].to[MonsterTruckBotEnvironment]
    )

}

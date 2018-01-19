import com.google.inject.AbstractModule
import data.DatabaseCreation
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

    override def configure(): Unit = {
        bind(classOf[DatabaseCreation]).asEagerSingleton()
    }

    //    override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    //
    //    )

}

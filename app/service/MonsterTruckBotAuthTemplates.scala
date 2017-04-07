package service

import com.cobble.bot.common.models.User
import controllers.WebJarAssets
import play.api.Configuration
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import securesocial.controllers.ViewTemplates
import securesocial.core.RuntimeEnvironment

class MonsterTruckBotAuthTemplates(env: RuntimeEnvironment)(implicit configuration: Configuration, webJarAssets: WebJarAssets, override val messagesApi: MessagesApi) extends ViewTemplates.Default(env)(configuration) with I18nSupport {

    override def getNotAuthorizedPage(implicit request: RequestHeader, lang: Lang): Html = {
        implicit val userOpt: Option[User] = None
        views.html.auth.notAuthorized()
    }

}
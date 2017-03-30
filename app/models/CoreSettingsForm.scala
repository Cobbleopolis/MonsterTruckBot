package models

import play.api.data._
import play.api.data.Forms._

case class CoreSettingsForm(twitchAccount: Option[String], moderatorRole: Option[String])

object CoreSettingsForm {

    val form = Form(
        mapping(
            "twitchAccount" -> optional(text),
            "moderatorRole" -> optional(text)
        )(apply)(unapply)
    )

}

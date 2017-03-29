package models

import play.api.libs.json._

case class UserServersResponse(owner: Boolean, permissions: Int, icon: Option[String], id: String, name: String)

object UserServersResponse {

    implicit val jsonParser: OFormat[UserServersResponse] = Json.format[UserServersResponse]

}

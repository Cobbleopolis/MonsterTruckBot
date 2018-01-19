package actions

import javax.inject.Inject

import common.ref.MtrConfigRef
import discord.DiscordBot
import org.webjars.play.WebJarsUtil
import play.api.mvc.Results._
import play.api.mvc._
import sx.blah.discord.handle.obj.Permissions
import util.AuthUtil

import scala.concurrent.{ExecutionContext, Future}

class SecureAction @Inject()(ec: ExecutionContext, webJarsUtil: WebJarsUtil, discordBot: DiscordBot, authUtil: AuthUtil, mtrConfigRef: MtrConfigRef) extends ActionFilter[MessagesRequest] {

    override def filter[A](request: MessagesRequest[A]): Future[Option[Result]] = {
        val requestUserIdOpt: Option[String] = request.session.get("userId")
        if (requestUserIdOpt.isEmpty || discordBot.guild.isEmpty || authUtil.getAccessToken(requestUserIdOpt.get).isEmpty) {
            Future.successful(Some(Unauthorized(views.html.auth.unauthorized()(webJarsUtil, discordBot, request.asInstanceOf[MessagesRequest[AnyContent]]))))
        } else {
            val userIdLong: Long = java.lang.Long.parseUnsignedLong(requestUserIdOpt.get)
            if (discordBot.client.getUserByID(userIdLong) != null) {
                val userPermissions = discordBot.client.getUserByID(userIdLong)
                    .getPermissionsForGuild(discordBot.guild.get)
                if ((mtrConfigRef.maintainerUserId.isDefined && mtrConfigRef.maintainerUserId.get == userIdLong) ||
                    discordBot.guild.get.getOwnerLongID == userIdLong ||
                    userPermissions.contains(Permissions.MANAGE_SERVER) ||
                    userPermissions.contains(Permissions.ADMINISTRATOR))
                    Future.successful(None)
                else
                    Future.successful(Some(Forbidden(views.html.auth.forbidden()(webJarsUtil, discordBot, request.asInstanceOf[MessagesRequest[AnyContent]]))))
            } else
                Future.successful(Some(Forbidden(views.html.auth.forbidden()(webJarsUtil, discordBot, request.asInstanceOf[MessagesRequest[AnyContent]]))))
        }
    }

    override protected def executionContext: ExecutionContext = ec
}

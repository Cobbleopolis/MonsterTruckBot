package actions

import javax.inject.Inject
import common.ref.MtrConfigRef
import discord.DiscordBot
import org.webjars.play.WebJarsUtil
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._
import util.AuthUtil

import scala.concurrent.{ExecutionContext, Future}

class SecureAction @Inject()(
                                ec: ExecutionContext,
                                discordBot: DiscordBot,
                                authUtil: AuthUtil,
                                mtrConfigRef: MtrConfigRef,
                                forbiddenTemplate: views.html.auth.forbidden,
                                unauthorizedTemplate: views.html.auth.unauthorized
                            ) extends ActionFilter[MessagesRequest] {

    override def filter[A](request: MessagesRequest[A]): Future[Option[Result]] = {
        Future.successful(None)
//        Logger.info(request.session.data.toString)
//        val requestUserIdOpt: Option[String] = request.session.get("userId")
//        if (requestUserIdOpt.isEmpty || discordBot.guild.isEmpty || authUtil.getAccessToken(requestUserIdOpt.get).isEmpty) {
//            Future.successful(Some(Unauthorized(unauthorizedTemplate(request.path)(request.messages, request.session, request))))
//        } else {
//            Future.successful(None)
//            val userIdLong: Long = java.lang.Long.parseUnsignedLong(requestUserIdOpt.get)
//            if (discordBot.futureClient.getUserByID(userIdLong) != null) {
//                val userPermissions = discordBot.futureClient.getUserByID(userIdLong)
//                    .getPermissionsForGuild(discordBot.guild.get)
//                if ((mtrConfigRef.maintainerUserId.isDefined && mtrConfigRef.maintainerUserId.get == userIdLong) ||
//                    discordBot.guild.get.getOwnerLongID == userIdLong ||
//                    userPermissions.contains(Permissions.MANAGE_SERVER) ||
//                    userPermissions.contains(Permissions.ADMINISTRATOR))
//                    Future.successful(None)
//                else
//                    Future.successful(Some(Forbidden(forbiddenTemplate(request.path)(request.messages, request.session, request))))
//            } else
//                Future.successful(Some(Forbidden(forbiddenTemplate(request.path)(request.messages, request.session, request))))
//        }
    }

    override protected def executionContext: ExecutionContext = ec
}

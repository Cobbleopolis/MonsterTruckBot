package discord.commands

import common.DefaultLang
import common.api.commands.SoSCommand
import common.ref.MtrConfigRef
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import javax.inject.Inject
import play.api.i18n.MessagesApi

import scala.collection.JavaConverters._

class DiscordSoSCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, conf: MtrConfigRef, messagesApi: MessagesApi) extends DiscordCommand with SoSCommand with DefaultLang {

    override def execute(implicit event: DiscordCommandExecutionEvent): Unit = {
        if (event.getMessage.getChannel.isPrivate)
            messageUtil.replyDM("bot.sos.send.notDM")
        else
            try {
                event.getMessage.delete()
                event.getMessage.getGuild.getUsersByRole(event.getMessage.getGuild.getRoleByID(conf.moderatorRoleId)).asScala
                    .filterNot(_.isBot)
                    .foreach(user =>
                        messageUtil.sendDM(user, "bot.sos.message", event.getUser.mention(), event.getMessage.getChannel.mention(), if (event.getArgs.length > 0) event.getArgs.mkString(" ") else messagesApi("global.notAvailable"))
                    )
                messageUtil.replyDM("bot.sos.send.success")
            } catch {
                case _: Exception => messageUtil.replyDM("bot.sos.send.failure")
            }
    }
}

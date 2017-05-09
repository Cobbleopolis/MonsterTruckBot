package discord.commands

import javax.inject.Inject

import com.cobble.bot.common.api.commands.SoSCommand
import com.cobble.bot.common.models.CoreSettings
import discord.api.DiscordCommand
import discord.event.CommandExecutionEvent
import discord.util.DiscordMessageUtil
import play.api.db.Database
import play.api.i18n.MessagesApi

import scala.collection.JavaConverters._

class DiscordSoSCommand @Inject()(implicit val messageUtil: DiscordMessageUtil, db: Database, messagesApi: MessagesApi) extends DiscordCommand with SoSCommand {

    override def execute(implicit event: CommandExecutionEvent): Unit = {
        if (event.getMessage.getChannel.isPrivate)
            messageUtil.replyDM("bot.sos.send.notDM")
        else
            try {
                event.getMessage.delete()
                val coreSettingsOpt: Option[CoreSettings] = CoreSettings.get(event.getMessage.getGuild.getID)
                if (coreSettingsOpt.isDefined)
                    if (coreSettingsOpt.get.moderatorRoleId.isDefined) {
                        event.getMessage.getGuild.getUsersByRole(event.getMessage.getGuild.getRoleByID(coreSettingsOpt.get.moderatorRoleId.get)).asScala
                            .filterNot(_.isBot)
                            .foreach(user =>
                                messageUtil.sendDM(user, "bot.sos.message", event.getUser.mention(), event.getMessage.getChannel.mention(), if (event.getArgs.length > 0) event.getArgs.mkString(" ") else messagesApi("global.notAvailable"))
                            )
                        messageUtil.replyDM("bot.sos.send.success")
                    } else
                        messageUtil.replyDM("bot.sos.send.noModRole")
                else
                    messageUtil.replyDM("bot.sos.send.serverNotFound")
            } catch {
                case _: Exception => messageUtil.replyDM("bot.sos.send.failure")
            }
    }
}

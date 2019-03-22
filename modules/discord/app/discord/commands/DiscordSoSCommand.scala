package discord.commands

import com.google.inject.Provider
import common.DefaultLang
import common.api.commands.SoSCommand
import common.ref.MtrConfigRef
import discord.DiscordBot
import discord.api.DiscordCommand
import discord.event.DiscordCommandExecutionEvent
import discord.util.DiscordMessageUtil
import discord4j.core.`object`.entity.{Channel, Member, Message}
import javax.inject.Inject
import org.reactivestreams.Publisher
import play.api.i18n.MessagesApi
import reactor.core.scala.publisher._

class DiscordSoSCommand @Inject()(discordBot: Provider[DiscordBot])(implicit val messageUtil: DiscordMessageUtil, conf: MtrConfigRef, messagesApi: MessagesApi) extends DiscordCommand with SoSCommand with DefaultLang {

    override def execute(implicit event: DiscordCommandExecutionEvent): Publisher[_] = {
        messageUtil.replyNoAt("Working on that one...")
        val channelType: Channel.Type = event.getMessage.getChannel.block().getType
        if (channelType == Channel.Type.DM || channelType == Channel.Type.GROUP_DM)
            messageUtil.replyDM("bot.sos.send.notDM")
        else
            try {
                var pub = Mono(event.getMessage.delete())
                    .thenMany(messageUtil.replyDM("bot.sos.send.success"))
                if (discordBot.get().guild.isDefined) {
                    pub = pub.thenMany(
                        discordBot.get().guild.get.getMembers
                            .filter(m => m.getRoleIds.contains(discordBot.get.moderatorRoleSnowflake))
                            .flatMap[Message]((m: Member) =>
                            messageUtil.sendDM(m, "bot.sos.message",
                                event.getUser.getMention,
                                s"<#${event.getMessage.getChannel.block().getId.asString()}>",
                                if (event.getArgs.nonEmpty)
                                    event.getArgs.mkString(" ")
                                else
                                    messagesApi("global.notAvailable")
                            )
                        )
                    )
                }
                pub
            } catch {
                case _: Exception => messageUtil.replyDM("bot.sos.send.failure")
            }
    }
}

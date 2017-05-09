package controllers

import javax.inject.Inject

import auth.HasPermission
import com.cobble.bot.common.models.{BotInstance, CoreSettings, User}
import discord.DiscordBot
import models.CoreSettingsForm
import play.api.Configuration
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import securesocial.core.SecureSocial
import service.MonsterTruckBotEnvironment
import sx.blah.discord.handle.obj.IGuild
import util.AuthUtil


class Dashboard @Inject()(implicit db: Database, webJarAssets: WebJarAssets, override val env: MonsterTruckBotEnvironment, ws: WSClient, messages: MessagesApi, discordBot: DiscordBot, configuration: Configuration, authUtil: AuthUtil) extends Controller with SecureSocial with I18nSupport {

    def dashboard(guildId: String): Action[AnyContent] = SecuredAction(HasPermission(guildId)) { implicit request =>
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        val botInstanceOpt: Option[BotInstance] = BotInstance.get(guildId)
        val coreSettings: Option[CoreSettings] = CoreSettings.get(guildId)
        val guild: IGuild = discordBot.client.getGuildByID(guildId)
        if (botInstanceOpt.isDefined && coreSettings.isDefined && guild != null) {
            val coreSettingsFill: CoreSettingsForm = CoreSettingsForm(botInstanceOpt.get.twitchAccount, coreSettings.get.moderatorRoleId)
            Ok(views.html.dashboard(guild, CoreSettingsForm.form.fill(coreSettingsFill)))
        } else
            Redirect(discordBot.getInviteLink(guildId, routes.Application.createBot().absoluteURL()))
    }

    def submitCoreSettings(guildId: String): Action[AnyContent] = SecuredAction(HasPermission(guildId)) { implicit request =>
        implicit val userOpt: Option[User] = Some(request.user.asInstanceOf[User])
        CoreSettingsForm.form.bindFromRequest().fold(
            formWithErrors => {
                val guild: IGuild = discordBot.client.getGuildByID(guildId)
                BadRequest(views.html.dashboard(guild, formWithErrors))
            },
            coreSettingsForm => {
                BotInstance.update(guildId, 'twitch_account -> coreSettingsForm.twitchAccount)
                CoreSettings.update(guildId, 'moderator_role_id -> coreSettingsForm.moderatorRole)
                Redirect(routes.Dashboard.dashboard(guildId)).flashing("success" -> messages("dashboard.settingsSaved", messages("dashboard.core")))
            }
        )
    }

    override def messagesApi: MessagesApi = messages
}

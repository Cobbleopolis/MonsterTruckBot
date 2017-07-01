package controllers

import javax.inject.Inject

import auth.HasPermission
import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import discord.DiscordBot
import models.DashboardSettingsForms
import play.api.cache.CacheApi
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import securesocial.core.{BasicProfile, SecureSocial}
import service.MonsterTruckBotEnvironment
import sx.blah.discord.handle.obj.IGuild
import util.AuthUtil


class Dashboard @Inject()(implicit db: Database, cache: CacheApi, webJarAssets: WebJarAssets, override val env: MonsterTruckBotEnvironment, ws: WSClient, dashboardSettingsForms: DashboardSettingsForms, messages: MessagesApi, discordBot: DiscordBot, config: MtrConfigRef, authUtil: AuthUtil) extends Controller with SecureSocial with I18nSupport {

    def dashboard(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val filterSettings: Option[FilterSettings] = FilterSettings.get(config.guildId)
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (filterSettings.isDefined
            && guild != null
        ) {
            Ok(views.html.dashboard(guild,
                dashboardSettingsForms.filterForm.fill(filterSettings.get),
                dashboardSettingsForms.newCommandForm
            ))
        } else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def submitFilterSettings(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        dashboardSettingsForms.filterForm.bindFromRequest().fold(
            formWithErrors => {
                val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
                BadRequest(views.html.dashboard(guild, formWithErrors, dashboardSettingsForms.newCommandForm))
            },
            filterSettings => {
                FilterSettings.update(filterSettings.guildId, filterSettings)
                Redirect(routes.Dashboard.dashboard()).flashing("success" -> messages("dashboard.settingsSaved", messages("dashboard.filter")))
            }
        )

    }

    def submitNewCommand(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val filterSettings: Option[FilterSettings] = FilterSettings.get(config.guildId)
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        dashboardSettingsForms.newCommandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard(guild, dashboardSettingsForms.filterForm.fill(filterSettings.get), formWithErrors))
            },
            newCustomCommand => {
                if (CustomCommand.get(config.guildId, newCustomCommand.commandName).isDefined)
                    Redirect(routes.Dashboard.dashboard()).flashing("danger" -> messages("dashboard.forms.customCommands.newCommand.errors.commandExists"))
                else {
                    CustomCommand.insert(newCustomCommand)
                    Redirect(routes.Dashboard.dashboard()).flashing("success" -> messages("dashboard.forms.customCommands.newCommand.saved"))
                }
            }
        )
    }

    override def messagesApi: MessagesApi = messages
}

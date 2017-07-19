package controllers

import javax.inject.Inject

import auth.HasPermission
import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import discord.DiscordBot
import models.DashboardSettingsForms
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc._
import securesocial.core.{BasicProfile, SecureSocial}
import service.MonsterTruckBotEnvironment
import sx.blah.discord.handle.obj.IGuild
import util.AuthUtil


class Dashboard @Inject()(implicit controllerComponents: ControllerComponents, db: Database, cache: SyncCacheApi, webJarAssets: WebJarAssets, override val env: MonsterTruckBotEnvironment, ws: WSClient, dashboardSettingsForms: DashboardSettingsForms, messages: MessagesApi, discordBot: DiscordBot, config: MtrConfigRef, authUtil: AuthUtil) extends AbstractController(controllerComponents) with SecureSocial with I18nSupport {

    def dashboard(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (guild != null)
            Ok(views.html.dashboard.coreSettings(guild))
        else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def filterSettings(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val filterSettings: Option[FilterSettings] = FilterSettings.get(config.guildId)
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (filterSettings.isDefined
            && guild != null
        ) {
            Ok(views.html.dashboard.filterSettings(guild,
                dashboardSettingsForms.filterForm.fill(filterSettings.get)
            ))
        } else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def submitFilterSettings(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        dashboardSettingsForms.filterForm.bindFromRequest().fold(
            formWithErrors => {
                val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
                BadRequest(views.html.dashboard.filterSettings(guild, formWithErrors))
            },
            filterSettings => {
                FilterSettings.update(filterSettings.guildId, filterSettings)
                Redirect(routes.Dashboard.filterSettings()).flashing("success" -> messages("dashboard.settingsSaved", messages("dashboard.filter")))
            }
        )

    }

    def customCommands(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (guild != null) {
            val commandForms = CustomCommand.getByGuildId(config.guildId).map(dashboardSettingsForms.commandForm.fill)
            Ok(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm, commandForms))
        } else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def customCommandRedirect(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        Redirect(routes.Dashboard.customCommands())
    }

    def submitNewCommand(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        val commandForms = CustomCommand.getByGuildId(config.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(guild, formWithErrors, commandForms))
            },
            newCustomCommand => {
                if (CustomCommand.get(config.guildId, newCustomCommand.commandName).isDefined)
                    BadRequest(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm.fill(newCustomCommand).withError(dashboardSettingsForms.existingCommandFormError), commandForms))
                else {
                    CustomCommand.insert(newCustomCommand)
                    Redirect(routes.Dashboard.customCommands()).flashing("success" -> messages("dashboard.forms.customCommands.newCommand.saved"))
                }
            }
        )
    }

    def submitEditCommand(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        val commandForms = CustomCommand.getByGuildId(config.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm, commandForms.map(form => if (form("commandName").value == formWithErrors("commandName").value) formWithErrors else form)))
            },
            editCustomCommand => {
                CustomCommand.update(editCustomCommand)
                Redirect(routes.Dashboard.customCommands()).flashing("success" -> messages("dashboard.forms.customCommands.editCommand.saved", config.commandPrefix, editCustomCommand.commandName))
            }
        )
    }

    def submitDeleteCommand(commandName: String): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        CustomCommand.delete(config.guildId, commandName)
        Redirect(routes.Dashboard.customCommands()).flashing("success" -> messages("dashboard.forms.customCommands.deleteCommand.commandDeleted", config.commandPrefix, commandName))
    }

    override def messagesApi: MessagesApi = messages
}

object Dashboard {
    val dashboardPages: Map[String, String] = Map(
        "filterSettings" -> "dashboard.forms.filters.title",
        "customCommands" -> "dashboard.forms.customCommands.title"
    )
}
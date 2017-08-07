package controllers

import javax.inject.Inject

import com.cobble.bot.common.models.{CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import discord.DiscordBot
import models.DashboardSettingsForms
import org.webjars.play.WebJarsUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.libs.ws.WSClient
import play.api.mvc._
import sx.blah.discord.handle.obj.IGuild


class Dashboard @Inject()(
                             implicit cc: ControllerComponents,
                             messagesAction: MessagesActionBuilder,
                             db: Database, cache: SyncCacheApi,
                             webJarsUtil: WebJarsUtil,
                             ws: WSClient,
                             dashboardSettingsForms: DashboardSettingsForms,
                             discordBot: DiscordBot,
                             config: MtrConfigRef
                         ) extends AbstractController(cc) {

    def dashboard(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (guild != null)
            Ok(views.html.dashboard.coreSettings(guild))
        else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def filterSettings(): Action[AnyContent] = messagesAction { implicit request =>
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

    def submitFilterSettings(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        dashboardSettingsForms.filterForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.filterSettings(guild, formWithErrors))
            },
            filterSettings => {
                val numUpdated: Int = FilterSettings.update(filterSettings.guildId, filterSettings)
                if(numUpdated != 0)
                    Redirect(routes.Dashboard.filterSettings()).flashing("success" -> request.messages("dashboard.settingsSaved", request.messages("dashboard.filter")))
                else {
                    val filterSettings: Option[FilterSettings] = FilterSettings.get(config.guildId)
                    InternalServerError(views.html.dashboard.filterSettings(guild, dashboardSettingsForms.filterForm.fill(filterSettings.get)))
                }
            }
        )
    }

    def customCommands(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (guild != null) {
            val commandForms = CustomCommand.getByGuildId(config.guildId).map(dashboardSettingsForms.commandForm.fill)
            Ok(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm, commandForms))
        } else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def customCommandRedirect(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        Redirect(routes.Dashboard.customCommands())
    }

    def submitNewCommand(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
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
                    Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.forms.customCommands.newCommand.saved"))
                }
            }
        )
    }

    def submitEditCommand(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        val commandForms = CustomCommand.getByGuildId(config.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm, commandForms.map(form => if (form("commandName").value == formWithErrors("commandName").value) formWithErrors else form)))
            },
            editCustomCommand => {
                CustomCommand.update(editCustomCommand)
                Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.forms.customCommands.editCommand.saved", config.commandPrefix, editCustomCommand.commandName))
            }
        )
    }

    def submitDeleteCommand(commandName: String): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        CustomCommand.delete(config.guildId, commandName)
        Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.forms.customCommands.deleteCommand.commandDeleted", config.commandPrefix, commandName))
    }

    def bitTracking(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (guild != null)
            Ok(views.html.dashboard.bitTracking(guild))
        else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }
}

object Dashboard {
    val dashboardPages: Map[String, String] = Map(
        "filterSettings" -> "dashboard.forms.filters.title",
        "customCommands" -> "dashboard.forms.customCommands.title",
        "bitTracking" -> "dashboard.forms.bitTracking.title"
    )
}
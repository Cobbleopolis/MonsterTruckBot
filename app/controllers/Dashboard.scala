package controllers

import javax.inject.Inject

import com.cobble.bot.common.models.{BitTrackingSettings, CustomCommand, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import com.cobble.bot.common.util.BitTrackingUtil
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
                             mtrConfigRef: MtrConfigRef,
                             bitTrackingUtil: BitTrackingUtil,
                         ) extends AbstractController(cc) {

    def dashboard(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        Ok(views.html.dashboard.coreSettings(guild))
    }

    def filterSettings(): Action[AnyContent] = messagesAction { implicit request =>
        val filterSettingsOpt: Option[FilterSettings] = FilterSettings.get(mtrConfigRef.guildId)
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        if (filterSettingsOpt.isDefined)
            Ok(views.html.dashboard.filterSettings(guild, dashboardSettingsForms.filterForm.fill(filterSettingsOpt.get)))
        else
            InternalServerError(views.html.dashboard.settingsMissing(guild, "filterSettings"))
    }

    def submitFilterSettings(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        dashboardSettingsForms.filterForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.filterSettings(guild, formWithErrors))
            },
            filterSettings => {
                val numUpdated: Int = FilterSettings.update(filterSettings.guildId, filterSettings)
                if(numUpdated != 0)
                    Redirect(routes.Dashboard.filterSettings()).flashing("success" -> request.messages("dashboard.settingsSaved", request.messages("dashboard.filter")))
                else {
                    val filterSettings: Option[FilterSettings] = FilterSettings.get(mtrConfigRef.guildId)
                    InternalServerError(views.html.dashboard.filterSettings(guild, dashboardSettingsForms.filterForm.fill(filterSettings.get)))
                }
            }
        )
    }

    def customCommands(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        val commandForms = CustomCommand.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        Ok(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm, commandForms))
    }

    def customCommandRedirect(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        Redirect(routes.Dashboard.customCommands())
    }

    def submitNewCommand(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        val commandForms = CustomCommand.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(guild, formWithErrors, commandForms))
            },
            newCustomCommand => {
                if (CustomCommand.get(mtrConfigRef.guildId, newCustomCommand.commandName).isDefined)
                    BadRequest(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm.fill(newCustomCommand).withError(dashboardSettingsForms.existingCommandFormError), commandForms))
                else {
                    CustomCommand.insert(newCustomCommand)
                    Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.forms.customCommands.newCommand.saved"))
                }
            }
        )
    }

    def submitEditCommand(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        val commandForms = CustomCommand.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(guild, dashboardSettingsForms.commandForm, commandForms.map(form => if (form("commandName").value == formWithErrors("commandName").value) formWithErrors else form)))
            },
            editCustomCommand => {
                CustomCommand.update(editCustomCommand)
                Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.forms.customCommands.editCommand.saved", mtrConfigRef.commandPrefix, editCustomCommand.commandName))
            }
        )
    }

    def submitDeleteCommand(commandName: String): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        CustomCommand.delete(mtrConfigRef.guildId, commandName)
        Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.forms.customCommands.deleteCommand.commandDeleted", mtrConfigRef.commandPrefix, commandName))
    }

    def bitTracking(): Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            Ok(views.html.dashboard.bitTracking(guild, dashboardSettingsForms.bitTrackingForm.fill(bitTrackingUtil.getBitTrackingFormData), bitTrackingUtil))
        else
            InternalServerError(views.html.dashboard.settingsMissing(guild, "bitTracking"))
    }

    def submitBitTracking = messagesAction { implicit request: MessagesRequest[AnyContent] =>
        val guild: IGuild = discordBot.client.getGuildByID(mtrConfigRef.guildId)
        dashboardSettingsForms.bitTrackingForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.bitTracking(guild, formWithErrors, bitTrackingUtil))
            },
            bitTrackingFormData => {
                bitTrackingUtil.setBitTrackingFormData(bitTrackingFormData)
                BitTrackingSettings.update(mtrConfigRef.guildId, bitTrackingFormData.getBitTrackingSettings)
                Redirect(routes.Dashboard.bitTracking()).flashing("success" -> request.messages("dashboard.forms.bitTracking.saved"))
            }
        )
    }
}

object Dashboard {
    val dashboardPages: Map[String, String] = Map(
        "filterSettings" -> "dashboard.forms.filters.title",
        "customCommands" -> "dashboard.forms.customCommands.title",
        "bitTracking" -> "dashboard.forms.bitTracking.title"
    )
}
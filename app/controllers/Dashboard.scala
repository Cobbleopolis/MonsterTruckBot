package controllers

import actions.SecureAction
import common.api.bitTracking.BitTrackingState
import common.components.DaoComponents
import common.models.{BitTrackingSettings, CustomCommand, FilterSettings, TwitchRegular}
import common.ref.MtrConfigRef
import components.DashboardTemplateComponents
import discord.DiscordBot
import javax.inject.Inject
import models.DashboardSettingsForms
import play.api.Logger
import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures._
import play.api.libs.ws.WSClient
import play.api.mvc._
import twitch.TwitchBot

import scala.concurrent.ExecutionContext


class Dashboard @Inject()(
                             implicit cc: ControllerComponents,
                             messagesAction: MessagesActionBuilder,
                             secureAction: SecureAction,
                             ws: WSClient,
                             daoComponent: DaoComponents,
                             dashboardSettingsForms: DashboardSettingsForms,
                             dtc: DashboardTemplateComponents,
                             discordBot: DiscordBot,
                             twitchBot: TwitchBot,
                             mtrConfigRef: MtrConfigRef,
                             bitTrackingState: BitTrackingState,
                             ec: ExecutionContext,
                             futures: Futures
                         ) extends AbstractController(cc) {

    def dashboard(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        Ok(dtc.coreSettingsTemplate())
    }

    def filterSettings(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request =>
        val filterSettingsOpt: Option[FilterSettings] = daoComponent.filterSettingsDAO.get(mtrConfigRef.guildId)
        if (filterSettingsOpt.isDefined)
            Ok(dtc.filterSettingsTemplate(dashboardSettingsForms.filterForm.fill(filterSettingsOpt.get)))
        else
            InternalServerError(dtc.settingsMissingTemplate("filterSettings"))
    }

    def submitFilterSettings(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        dashboardSettingsForms.filterForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(dtc.filterSettingsTemplate(formWithErrors))
            },
            filterSettings => {
                val numUpdated: Int = daoComponent.filterSettingsDAO.update(filterSettings.guildId, filterSettings)
                if (numUpdated != 0)
                    Redirect(routes.Dashboard.filterSettings()).flashing("success" -> "dashboard.filters.saved")
                else {
                    val filterSettings: Option[FilterSettings] = daoComponent.filterSettingsDAO.get(mtrConfigRef.guildId)
                    InternalServerError(dtc.filterSettingsTemplate(dashboardSettingsForms.filterForm.fill(filterSettings.get)))
                }
            }
        )
    }

    def customCommands(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val commandForms = daoComponent.customCommandDAO.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        Ok(dtc.customCommandTemplate(dashboardSettingsForms.commandForm, commandForms))
    }

    def customCommandRedirect(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        Redirect(routes.Dashboard.customCommands())
    }

    def submitNewCommand(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val commandForms = daoComponent.customCommandDAO.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(dtc.customCommandTemplate(formWithErrors, commandForms))
            },
            newCustomCommandRaw => {
                val newCustomCommand: CustomCommand = newCustomCommandRaw.copy(commandName = newCustomCommandRaw.commandName.toLowerCase())
                if (daoComponent.customCommandDAO.get(mtrConfigRef.guildId, newCustomCommand.commandName).isDefined)
                    BadRequest(dtc.customCommandTemplate(dashboardSettingsForms.commandForm.fill(newCustomCommand).withError(dashboardSettingsForms.existingCommandFormError), commandForms))
                else {
                    daoComponent.customCommandDAO.insert(newCustomCommand)
                    Redirect(routes.Dashboard.customCommands()).flashing("success" -> "dashboard.customCommands.newCommand.saved")
                }
            }
        )
    }

    def submitEditCommand(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val commandForms = daoComponent.customCommandDAO.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(dtc.customCommandTemplate(dashboardSettingsForms.commandForm, commandForms.map(form => if (form("commandName").value == formWithErrors("commandName").value) formWithErrors else form)))
            },
            editCustomCommand => {
                daoComponent.customCommandDAO.update(editCustomCommand)
                Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.customCommands.editCommand.saved", mtrConfigRef.commandPrefix, editCustomCommand.commandName))
            }
        )
    }

    def submitDeleteCommand(commandName: String): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        daoComponent.customCommandDAO.delete(mtrConfigRef.guildId, commandName)
        Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.customCommands.deleteCommand.commandDeleted", mtrConfigRef.commandPrefix, commandName))
    }

    def bitTracking(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = daoComponent.bitTrackingSettingsDAO.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            Ok(dtc.bitTrackingTemplate(dashboardSettingsForms.bitTrackingForm.fill(bitTrackingState.getBitTrackingFormData), bitTrackingState))
        else
            InternalServerError(dtc.settingsMissingTemplate("bitTracking"))
    }

    def submitBitTracking: Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        dashboardSettingsForms.bitTrackingForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(dtc.bitTrackingTemplate(formWithErrors, bitTrackingState))
            },
            bitTrackingFormData => {
                Logger.info("Update")
                daoComponent.bitTrackingSettingsDAO.update(mtrConfigRef.guildId, bitTrackingFormData.getBitTrackingSettings)
                bitTrackingState.setBitTrackingFormData(bitTrackingFormData)
                Redirect(routes.Dashboard.bitTracking()).flashing("success" -> "dashboard.bitTracking.saved")
            }
        )
    }

    def twitchRegulars: Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val twitchRegulars: List[TwitchRegular] = daoComponent.twitchRegularDAO.getByGuildId(mtrConfigRef.guildId)
        Ok(dtc.twitchRegularTemplate(dashboardSettingsForms.twitchRegularForm, twitchRegulars))
    }

    def submitNewTwitchRegular: Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val twitchRegulars: List[TwitchRegular] = daoComponent.twitchRegularDAO.getByGuildId(mtrConfigRef.guildId)
        dashboardSettingsForms.twitchRegularForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(dtc.twitchRegularTemplate(formWithErrors, twitchRegulars))
            },
            twitchRegular => {
                if (twitchRegulars.exists(_.twitchUsername.equalsIgnoreCase(twitchRegular.twitchUsername))) {
                    BadRequest(dtc.twitchRegularTemplate(
                        dashboardSettingsForms.twitchRegularForm
                            .fill(twitchRegular)
                            .withError(dashboardSettingsForms.existingTwitchRegularFormError),
                        twitchRegulars
                    ))
                } else {
                    daoComponent.twitchRegularDAO.insert(twitchRegular)
                    Redirect(routes.Dashboard.twitchRegulars()).flashing("success" ->
                        request.messages("dashboard.twitchRegulars.newTwitchRegular.saved", twitchRegular.twitchUsername)
                    )
                }
            }
        )
    }

    def submitDeleteTwitchRegular(twitchUsername: String): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val deleteCount: Int = daoComponent.twitchRegularDAO.delete(mtrConfigRef.guildId, twitchUsername)
        val result: Result = Redirect(routes.Dashboard.twitchRegulars())
        if (deleteCount == 0)
            result.flashing("danger" -> request.messages("dashboard.twitchRegulars.deleteTwitchRegular.errorDeleting", twitchUsername))
        else
            result.flashing("success" -> request.messages("dashboard.twitchRegulars.deleteTwitchRegular.twitchRegularDeleted", twitchUsername))
    }

    def reconnectDiscord: Action[AnyContent] = (messagesAction andThen secureAction).async { implicit request: MessagesRequest[AnyContent] =>
        discordBot.reconnect().withTimeout(mtrConfigRef.botReconnectTimeout).map(_ => {
            if (discordBot.botClient.isConnected) {
                Redirect(routes.Dashboard.dashboard()).flashing("success" -> "dashboard.core.reconnect.discord.success")
            } else {
                Redirect(routes.Dashboard.dashboard()).flashing("danger" -> "dashboard.core.reconnect.discord.failure")
            }
        }).recover {
            case _: scala.concurrent.TimeoutException =>
                Redirect(routes.Dashboard.dashboard()).flashing("danger" -> "dashboard.core.reconnect.discord.timeout")
        }
    }

    def reconnectTwitch: Action[AnyContent] = (messagesAction andThen secureAction).async { implicit request: MessagesRequest[AnyContent] =>
        twitchBot.reconnect().withTimeout(mtrConfigRef.botReconnectTimeout).map(_ => {
            if (twitchBot.isConnected) {
                Redirect(routes.Dashboard.dashboard()).flashing("success" -> "dashboard.core.reconnect.twitch.success")
            } else {
                Redirect(routes.Dashboard.dashboard()).flashing("danger" -> "dashboard.core.reconnect.twitch.failure")
            }
        }).recover{
            case _: scala.concurrent.TimeoutException =>
                Redirect(routes.Dashboard.dashboard()).flashing("danger" -> "dashboard.core.reconnect.twitch.timeout")
        }
    }
}

object Dashboard {
    val dashboardPages: Map[String, String] = Map(
        "filterSettings" -> "dashboard.pageNames.filterSettings",
        "customCommands" -> "dashboard.pageNames.customCommands",
        "bitTracking" -> "dashboard.pageNames.bitTracking",
        "twitchRegulars" -> "dashboard.pageNames.twitchRegulars"
    )
}
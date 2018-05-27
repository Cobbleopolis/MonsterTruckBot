package controllers

import javax.inject.Inject
import actions.SecureAction
import common.api.bitTracking.BitTrackingState
import common.models.{BitTrackingSettings, CustomCommand, FilterSettings, TwitchRegular}
import common.ref.MtrConfigRef
import common.util.BitTrackingUtil
import discord.DiscordBot
import models.DashboardSettingsForms
import org.webjars.play.WebJarsUtil
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext


class Dashboard @Inject()(
                             implicit cc: ControllerComponents,
                             messagesAction: MessagesActionBuilder,
                             secureAction: SecureAction,
                             db: Database, cache: SyncCacheApi,
                             webJarsUtil: WebJarsUtil,
                             ws: WSClient,
                             dashboardSettingsForms: DashboardSettingsForms,
                             discordBot: DiscordBot,
                             mtrConfigRef: MtrConfigRef,
                             bitTrackingState: BitTrackingState,
                             ec: ExecutionContext
                         ) extends AbstractController(cc) {

    def dashboard(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        Ok(views.html.dashboard.coreSettings())
    }

    def filterSettings(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request =>
        val filterSettingsOpt: Option[FilterSettings] = FilterSettings.get(mtrConfigRef.guildId)
        if (filterSettingsOpt.isDefined)
            Ok(views.html.dashboard.filterSettings(dashboardSettingsForms.filterForm.fill(filterSettingsOpt.get)))
        else
            InternalServerError(views.html.dashboard.settingsMissing("filterSettings"))
    }

    def submitFilterSettings(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        dashboardSettingsForms.filterForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.filterSettings(formWithErrors))
            },
            filterSettings => {
                val numUpdated: Int = FilterSettings.update(filterSettings.guildId, filterSettings)
                if (numUpdated != 0)
                    Redirect(routes.Dashboard.filterSettings()).flashing("success" -> request.messages("dashboard.filters.saved"))
                else {
                    val filterSettings: Option[FilterSettings] = FilterSettings.get(mtrConfigRef.guildId)
                    InternalServerError(views.html.dashboard.filterSettings(dashboardSettingsForms.filterForm.fill(filterSettings.get)))
                }
            }
        )
    }

    def customCommands(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val commandForms = CustomCommand.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        Ok(views.html.dashboard.customCommands(dashboardSettingsForms.commandForm, commandForms))
    }

    def customCommandRedirect(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        Redirect(routes.Dashboard.customCommands())
    }

    def submitNewCommand(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val commandForms = CustomCommand.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(formWithErrors, commandForms))
            },
            newCustomCommandRaw => {
                val newCustomCommand: CustomCommand = newCustomCommandRaw.copy(commandName = newCustomCommandRaw.commandName.toLowerCase())
                if (CustomCommand.get(mtrConfigRef.guildId, newCustomCommand.commandName).isDefined)
                    BadRequest(views.html.dashboard.customCommands(dashboardSettingsForms.commandForm.fill(newCustomCommand).withError(dashboardSettingsForms.existingCommandFormError), commandForms))
                else {
                    CustomCommand.insert(newCustomCommand)
                    Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.customCommands.newCommand.saved"))
                }
            }
        )
    }

    def submitEditCommand(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val commandForms = CustomCommand.getByGuildId(mtrConfigRef.guildId).map(dashboardSettingsForms.commandForm.fill)
        dashboardSettingsForms.commandForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.customCommands(dashboardSettingsForms.commandForm, commandForms.map(form => if (form("commandName").value == formWithErrors("commandName").value) formWithErrors else form)))
            },
            editCustomCommand => {
                CustomCommand.update(editCustomCommand)
                Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.customCommands.editCommand.saved", mtrConfigRef.commandPrefix, editCustomCommand.commandName))
            }
        )
    }

    def submitDeleteCommand(commandName: String): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        CustomCommand.delete(mtrConfigRef.guildId, commandName)
        Redirect(routes.Dashboard.customCommands()).flashing("success" -> request.messages("dashboard.customCommands.deleteCommand.commandDeleted", mtrConfigRef.commandPrefix, commandName))
    }

    def bitTracking(): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val bitTrackingSettingsOpt: Option[BitTrackingSettings] = BitTrackingSettings.get(mtrConfigRef.guildId)
        if (bitTrackingSettingsOpt.isDefined)
            Ok(views.html.dashboard.bitTracking(dashboardSettingsForms.bitTrackingForm.fill(bitTrackingState.getBitTrackingFormData), bitTrackingState))
        else
            InternalServerError(views.html.dashboard.settingsMissing("bitTracking"))
    }

    def submitBitTracking: Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        dashboardSettingsForms.bitTrackingForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.bitTracking(formWithErrors, bitTrackingState))
            },
            bitTrackingFormData => {
                bitTrackingState.setBitTrackingFormData(bitTrackingFormData)
                Redirect(routes.Dashboard.bitTracking()).flashing("success" -> request.messages("dashboard.bitTracking.saved"))
            }
        )
    }

    def twitchRegulars: Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val twitchRegulars: List[TwitchRegular] = TwitchRegular.getByGuildId(mtrConfigRef.guildId)
        Ok(views.html.dashboard.twitchRegulars(dashboardSettingsForms.twitchRegularForm, twitchRegulars))
    }

    def submitNewTwitchRegular: Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val twitchRegulars: List[TwitchRegular] = TwitchRegular.getByGuildId(mtrConfigRef.guildId)
        dashboardSettingsForms.twitchRegularForm.bindFromRequest().fold(
            formWithErrors => {
                BadRequest(views.html.dashboard.twitchRegulars(formWithErrors, twitchRegulars))
            },
            twitchRegular => {
                if (twitchRegulars.exists(_.twitchUsername.equalsIgnoreCase(twitchRegular.twitchUsername))) {
                    BadRequest(views.html.dashboard.twitchRegulars(
                        dashboardSettingsForms.twitchRegularForm
                            .fill(twitchRegular)
                            .withError(dashboardSettingsForms.existingTwitchRegularFormError),
                        twitchRegulars
                    ))
                } else {
                    TwitchRegular.insert(twitchRegular)
                    Redirect(routes.Dashboard.twitchRegulars()).flashing("success" ->
                        request.messages("dashboard.twitchRegulars.newTwitchRegular.saved", twitchRegular.twitchUsername)
                    )
                }
            }
        )
    }

    def submitDeleteTwitchRegular(twitchUsername: String): Action[AnyContent] = (messagesAction andThen secureAction) { implicit request: MessagesRequest[AnyContent] =>
        val deleteCount: Int = TwitchRegular.delete(mtrConfigRef.guildId, twitchUsername)
        val result: Result = Redirect(routes.Dashboard.twitchRegulars())
        if (deleteCount == 0)
            result.flashing("danger" -> request.messages("dashboard.twitchRegulars.deleteTwitchRegular.errorDeleting", twitchUsername))
        else
            result.flashing("success" -> request.messages("dashboard.twitchRegulars.deleteTwitchRegular.twitchRegularDeleted", twitchUsername))
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
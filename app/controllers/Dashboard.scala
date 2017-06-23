package controllers

import javax.inject.Inject

import auth.HasPermission
import com.cobble.bot.common.models.{CoreSettings, FilterSettings}
import com.cobble.bot.common.ref.MtrConfigRef
import discord.DiscordBot
import models.{CoreSettingsForm, FilterSettingsForm}
import play.api.db.Database
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import securesocial.core.{BasicProfile, SecureSocial}
import service.MonsterTruckBotEnvironment
import sx.blah.discord.handle.obj.IGuild
import util.AuthUtil


class Dashboard @Inject()(implicit db: Database, webJarAssets: WebJarAssets, override val env: MonsterTruckBotEnvironment, ws: WSClient, messages: MessagesApi, discordBot: DiscordBot, config: MtrConfigRef, authUtil: AuthUtil) extends Controller with SecureSocial with I18nSupport {

    def dashboard(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        val coreSettings: Option[CoreSettings] = CoreSettings.get(config.guildId)
        val filterSettings: Option[FilterSettings] = FilterSettings.get(config.guildId)
        val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
        if (coreSettings.isDefined
            && filterSettings.isDefined
            && guild != null
        ) {
            Ok(views.html.dashboard(guild,
                CoreSettingsForm.form.fill(coreSettings.get),
                FilterSettingsForm.form.fill(filterSettings.get))
            )
        } else
            Redirect(discordBot.getInviteLink(routes.Dashboard.dashboard().absoluteURL()))
    }

    def submitCoreSettings(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>

        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        CoreSettingsForm.form.bindFromRequest().fold(
            formWithErrors => {
                val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
                BadRequest(views.html.dashboard(guild, formWithErrors, FilterSettingsForm.form.fill(FilterSettings.get(config.guildId).get)))
            },
            coreSettings => {
                CoreSettings.update(coreSettings.guildId, coreSettings.namedParameters: _*)
                Redirect(routes.Dashboard.dashboard()).flashing("success" -> messages("dashboard.settingsSaved", messages("dashboard.core")))
            }
        )
    }

    def submitFilterSettings(): Action[AnyContent] = SecuredAction(HasPermission()) { implicit request =>
        implicit val userOpt: Option[BasicProfile] = Some(request.user.asInstanceOf[BasicProfile])
        FilterSettingsForm.form.bindFromRequest().fold(
            formWithErrors => {
                val guild: IGuild = discordBot.client.getGuildByID(config.guildId)
                BadRequest(views.html.dashboard(guild, CoreSettingsForm.form.fill(CoreSettings.get(config.guildId).get), formWithErrors))
            },
            filterSettings => {
                FilterSettings.update(filterSettings.guildId, filterSettings.namedParameters: _*)
                Redirect(routes.Dashboard.dashboard()).flashing("success" -> messages("dashboard.settingsSaved", messages("dashboard.filter")))
            }
        )

    }

    override def messagesApi: MessagesApi = messages
}

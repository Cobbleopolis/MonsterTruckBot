package components

import javax.inject.Inject

case class DashboardTemplateComponents @Inject()(
                                                   coreSettingsTemplate: views.html.dashboard.coreSettings,
                                                   settingsMissingTemplate: views.html.dashboard.settingsMissing,
                                                   filterSettingsTemplate: views.html.dashboard.filterSettings,
                                                   customCommandTemplate: views.html.dashboard.customCommands,
                                                   bitTrackingTemplate: views.html.dashboard.bitTracking,
                                                   twitchRegularTemplate: views.html.dashboard.twitchRegulars
                                               )

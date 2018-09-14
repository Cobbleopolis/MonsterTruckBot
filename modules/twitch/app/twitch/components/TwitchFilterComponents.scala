package twitch.components

import javax.inject.Inject
import twitch.filters.{TwitchBlacklistFilter, TwitchCapsFilter, TwitchLinksFilter}

class TwitchFilterComponents @Inject()(
                                          val capsFilter: TwitchCapsFilter,
                                          val linksFilter: TwitchLinksFilter,
                                          val blacklistFilter: TwitchBlacklistFilter
                                      )

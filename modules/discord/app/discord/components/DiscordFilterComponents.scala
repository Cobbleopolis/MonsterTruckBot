package discord.components

import discord.filters.{DiscordBlacklistFilter, DiscordCapsFilter, DiscordLinksFilter}
import javax.inject.Inject

class DiscordFilterComponents @Inject()(
                                    val capsFilter: DiscordCapsFilter,
                                    val linksFilter: DiscordLinksFilter,
                                    val blacklistFilter: DiscordBlacklistFilter
                                )

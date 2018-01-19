package common.api.filters

import common.api.Filter
import common.models.FilterSettings

trait BlacklistFilter extends Filter {

    override def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean = filterSettings.blacklistFilterWords.split("\\R+").exists(word => word.nonEmpty && word.r.findFirstIn(messageContent).isDefined)

}

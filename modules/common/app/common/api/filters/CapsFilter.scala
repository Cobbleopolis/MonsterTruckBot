package common.api.filters

import common.api.Filter
import common.models.FilterSettings

trait CapsFilter extends Filter {

    override def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean = s"[\\p{javaUpperCase}\\s]{${filterSettings.capsFilterThreshold}}".r.findFirstIn(messageContent).isDefined

}

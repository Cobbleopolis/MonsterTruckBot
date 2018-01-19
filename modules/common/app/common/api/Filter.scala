package common.api

import common.models.FilterSettings

trait Filter {

    def doesMessageMatchFilter(messageContent: String, filterSettings: FilterSettings): Boolean

}

package filters

import javax.inject.Inject

import play.api.http.{DefaultHttpFilters, EnabledFilters}

class Filters @Inject()(defaultFilters: EnabledFilters, authFilter: AuthFilter) extends DefaultHttpFilters(defaultFilters.filters :+ authFilter: _*) {

}

package rifs.business.filters

import javax.inject.Inject

import play.api.http.DefaultHttpFilters

class Filters @Inject()(log: RequestLoggingFilter) extends DefaultHttpFilters(log)
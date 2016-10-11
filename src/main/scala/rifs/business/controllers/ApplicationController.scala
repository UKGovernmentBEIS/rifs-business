package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationOps
import rifs.models.OpportunityId

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(val cached: Cached, applications: ApplicationOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def forOpportunity(id: OpportunityId) = cacheOk {
    Action.async {
      applications.forOpportunity(id).map(jsonResult(_))
    }
  }
}

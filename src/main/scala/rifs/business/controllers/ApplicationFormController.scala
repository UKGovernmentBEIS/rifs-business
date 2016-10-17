package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationFormOps
import rifs.business.models.{ApplicationFormId, OpportunityId}

import scala.concurrent.ExecutionContext

class ApplicationFormController @Inject()(val cached: Cached, applicationForms: ApplicationFormOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def byId(id: ApplicationFormId) = cacheOk {
    Action.async {
      applicationForms.byId(id).map(jsonResult(_))
    }
  }

  def forOpportunity(id: OpportunityId) = cacheOk {
    Action.async {
      applicationForms.forOpportunity(id).map(jsonResult(_))
    }
  }
}

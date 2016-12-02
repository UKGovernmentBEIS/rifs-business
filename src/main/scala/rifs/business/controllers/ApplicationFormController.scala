package rifs.business.controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationFormOps
import rifs.business.models.{ApplicationFormId, OpportunityId}

import scala.concurrent.ExecutionContext

class ApplicationFormController @Inject()(applicationForms: ApplicationFormOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def byId(id: ApplicationFormId) = Action.async {
    applicationForms.byId(id).map(jsonResult(_))
  }

  def forOpportunity(id: OpportunityId) = Action.async {
    applicationForms.forOpportunity(id).map(jsonResult(_))
  }
}

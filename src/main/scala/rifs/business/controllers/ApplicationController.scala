package rifs.business.controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationOps
import rifs.models.OpportunityId

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(applications: ApplicationOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def forOpportunity(id: OpportunityId) = Action.async {
    applications.forOpportunity(id).map(jsonResult(_))
  }
}

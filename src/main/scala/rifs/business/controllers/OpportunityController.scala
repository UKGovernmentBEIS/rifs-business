package rifs.business.controllers

import javax.inject.Inject

import rifs.models.OpportunityId
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import rifs.business.OpportunityOps

import scala.concurrent.ExecutionContext

class OpportunityController @Inject()(opportunities: OpportunityOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def byId(id: OpportunityId) = Action.async {
    opportunities.byIdWithDescription(id).map(jsonResult(_))
  }

  def getOpen = Action.async {
    opportunities.open.map(os => Ok(Json.toJson(os)))
  }
}

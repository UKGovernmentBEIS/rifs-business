package ifs.data.controllers

import javax.inject.Inject

import ifs.data.db.OpportunityOps
import ifs.models.OpportunityId
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class OpportunityController @Inject()(opportunities: OpportunityOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def byId(id: OpportunityId) = Action.async {
    opportunities.byIdWithDescription(id).map(jsonResult(_))
  }

  def getOpen = Action.async {
    opportunities.open.map(os => Ok(Json.toJson(os)))
  }
}

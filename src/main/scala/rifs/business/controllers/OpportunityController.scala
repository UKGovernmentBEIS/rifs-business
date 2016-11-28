package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId
import rifs.business.restmodels.OpportunitySummary

import scala.concurrent.{ExecutionContext, Future}

class OpportunityController @Inject()(val cached: Cached, opportunities: OpportunityOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def byId(id: OpportunityId) = cacheOk {
    Action.async {
      opportunities.opportunity(id).map(jsonResult(_))
    }
  }

  def getOpenSummaries = cacheOk {
    Action.async {
      opportunities.openSummaries.map(os => Ok(Json.toJson(os)))
    }
  }

  def getOpen = cacheOk {
    Action.async {
      opportunities.open.map(os => Ok(Json.toJson(os)))
    }
  }

  def updateSummary(id: OpportunityId) = Action.async(parse.json[OpportunitySummary]) { implicit request =>
    val summary = request.body
    if (summary.id != id) Future.successful(BadRequest(s"id provided on url was ${id.id}, but does not match id of body: ${summary.id.id}"))
    else opportunities.updateSummary(request.body).map(_ => NoContent)
  }
}

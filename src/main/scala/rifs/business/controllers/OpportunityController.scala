package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId

import scala.concurrent.ExecutionContext

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
}

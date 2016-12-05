package rifs.business.controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import rifs.business.actions.OpportunityAction
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId
import rifs.business.restmodels.OpportunitySummary

import scala.concurrent.{ExecutionContext, Future}

class OpportunityController @Inject()(opportunities: OpportunityOps, OpportunityAction: OpportunityAction)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def byId(id: OpportunityId) = OpportunityAction(id)(request => Ok(Json.toJson(request.opportunity)))

  def getSummaries = Action.async(opportunities.summaries.map(os => Ok(Json.toJson(os))))

  def getOpenSummaries = Action.async(opportunities.openSummaries.map(os => Ok(Json.toJson(os))))

  def getOpen = Action.async(opportunities.findOpen.map(os => Ok(Json.toJson(os))))

  def updateSummary(id: OpportunityId) = Action.async(parse.json[OpportunitySummary]) { implicit request =>
    val summary = request.body
    if (summary.id != id) Future.successful(BadRequest(s"id provided on url was ${id.id}, but does not match id of body: ${summary.id.id}"))
    else opportunities.updateSummary(request.body).map(_ => NoContent)
  }

  def publish(id: OpportunityId) = OpportunityAction(id).async { implicit request =>
    request.opportunity.publishedAt match {
      case None => opportunities.publish(id).map {
        case Some(d) => Ok(Json.toJson(d))
        case None => NotFound
      }
      case Some(_) => Future.successful(BadRequest(s"Opportunity with id ${id.id} has already been published"))
    }
  }

  def duplicate(id: OpportunityId) = Action.async(opportunities.duplicate(id).map(jsonResult(_)))

  def saveDescription(id: OpportunityId, sectionNum: Int) = Action.async(parse.json[String]) { implicit request =>
    val description = request.body.trim match {
      case "" => None
      case s => Some(s)
    }

    opportunities.saveSectionDescription(id, sectionNum, description).map {
      case 0 => NotFound
      case _ => NoContent
    }
  }
}

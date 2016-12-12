package rifs.business.controllers

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import rifs.business.actions.OpportunityAction
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId
import rifs.business.notifications.NotificationService
import rifs.business.restmodels.OpportunitySummary

import scala.concurrent.{ExecutionContext, Future}

class OpportunityController @Inject()(opportunities: OpportunityOps,
                                      OpportunityAction: OpportunityAction,
                                      notifications: NotificationService)
                                     (implicit val ec: ExecutionContext) extends Controller
  with ControllerUtils with EmailUtils {

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
    import rifs.business.Config.config.rifs.{email => emailConfig}

    request.opportunity.publishedAt match {
      case None => opportunities.publish(id).flatMap {
        case Some(d) =>
          val mgrMail = emailConfig.dummymanager
          notifications.notifyManager(id, emailConfig.replyto, mgrMail).
            map { em =>
              if (em.isEmpty) Logger.warn("Failed to find the published opportunity")
            }.recover {
            case t =>
              Logger.error(s"Failed to send email to $mgrMail on an opportunity publishing", t)
              None
          }.map { _ => Ok(Json.toJson(d)) }

        case None => Future.successful(NotFound)
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

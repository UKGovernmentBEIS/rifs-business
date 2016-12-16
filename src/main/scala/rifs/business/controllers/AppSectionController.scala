package rifs.business.controllers

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import rifs.business.data.{ApplicationFormOps, ApplicationOps, OpportunityOps}
import rifs.business.models.ApplicationId
import rifs.business.notifications.NotificationService
import rifs.business.restmodels.ApplicationSectionDetail

import scala.concurrent.ExecutionContext

class AppSectionController @Inject()(applications: ApplicationOps,
                                     appForms: ApplicationFormOps,
                                     opps: OpportunityOps,
                                     notifications: NotificationService)
                                    (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def get(id: ApplicationId, sectionNumber: Int) =
    Action.async(applications.fetchSection(id, sectionNumber).map(jsonResult(_)))

  def sectionDetail(id: ApplicationId, sectionNumber: Int) = Action.async {
    val ft = for {
      a <- OptionT(applications.application(id))
      f <- OptionT(appForms.byId(a.applicationFormId))
      o <- OptionT(opps.opportunity(f.opportunityId))
      fs <- OptionT.fromOption(f.sections.find(_.sectionNumber == sectionNumber))
    } yield {
      ApplicationSectionDetail(
        a.id,
        f.sections.length,
        a.sections.count(_.completedAt.isDefined),
        o.summary,
        fs,
        a.sections.find(_.sectionNumber == sectionNumber))
    }

    ft.value.map(jsonResult(_))
  }

  def sections(id: ApplicationId) =
    Action.async(applications.fetchSections(id).map(os => Ok(Json.toJson(os))))

  def post(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body).map(_ => NoContent)
  }

  def clearSectionCompletedDate(id: ApplicationId, sectionNumber: Int) = Action.async { implicit request =>
    applications.clearSectionCompletedDate(id, sectionNumber).map { count =>
      if (count > 0) NoContent else NotFound
    }
  }

  def completeSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body, Some(DateTime.now(DateTimeZone.UTC))).map(_ => NoContent)
  }

  def delete(id: ApplicationId, sectionNumber: Int) = Action.async { implicit request =>
    applications.deleteSection(id, sectionNumber).map(_ => NoContent)
  }
}

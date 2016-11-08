package rifs.business.controllers

import javax.inject.Inject

import org.joda.time.LocalDateTime
import play.api.cache.Cached
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormId, ApplicationId}
import rifs.business.notifications.Notifications.EmailId
import rifs.business.notifications.{NotificationService, Notifications}

import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject()(val cached: Cached, applications: ApplicationOps, notifications: NotificationService)
                                     (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {
  def byId(id: ApplicationId) = cacheOk {
    Action.async(applications.byId(id).map(jsonResult(_)))
  }

  def applicationForForm(applicationFormId: ApplicationFormId) = Action.async {
    applications.forForm(applicationFormId).map(jsonResult(_))
  }

  /**
    * If an `Application` exists for the `ApplicationForm` then return it, otherwise create one.
    * If the `id` does not match an existing `ApplicationForm` then return a 404
    */
  def overview(applicationId: ApplicationId) =
    Action.async(applications.overview(applicationId).map(jsonResult(_)))

  def section(id: ApplicationId, sectionNumber: Int) =
    Action.async(applications.fetchSection(id, sectionNumber).map(jsonResult(_)))

  def sections(id: ApplicationId) =
    Action.async(applications.fetchSections(id).map(os => Ok(Json.toJson(os))))

  def saveSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body).map(_ => NoContent)
  }

  def completeSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body, Some(LocalDateTime.now())).map(_ => NoContent)
  }

  def submit(id: ApplicationId) = Action.async { _ =>
    val submission = applications.submit(id).flatMap { submissionRef =>

      val notify = Seq( notifications.notifyPortfolioManager(submissionRef, Notifications.ApplicationSubmitted) )
      val submissionJs = JsObject( Seq( ("ref"/* Todo */, JsString(submissionRef.id.toString) ) ) )
      val res = notify.foldLeft( Future { submissionJs  } ){
        case (jsFut, act) =>  jsFut.flatMap( js=> jsonFuture(act,{jsv=> js + ("e",jsv) })  )
      }

      res
    }
    jsonResult(submission)
  }

}

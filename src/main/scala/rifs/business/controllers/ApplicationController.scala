package rifs.business.controllers

import javax.inject.Inject

import org.joda.time.LocalDateTime
import play.api.cache.Cached
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormId, ApplicationId}

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(val cached: Cached, applications: ApplicationOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {
  def byId(id: ApplicationId) = cacheOk {
    Action.async(applications.byId(id).map(jsonResult(_)))
  }

  def applicationForForm(applicationFormId: ApplicationFormId) = Action.async {
    applications.forForm(applicationFormId).map(jsonResult(_))
  }

  def application(applicationId: ApplicationId) =
    Action.async(applications.application(applicationId).map(jsonResult(_)))

  val emptyJsObject: JsObject = JsObject(Seq())

  /**
    * Returns the structure of application with its sections, but without any answers.
    * Useful for situations where the client is just looking for the status of the
    * application and sections without needing the full content.
    */
  def overview(applicationId: ApplicationId) = Action.async {
    applications.application(applicationId).map {
      _.map(app => app.copy(sections = app.sections.map(_.copy(answers = emptyJsObject))))
    }.map(jsonResult(_))
  }

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

  def deleteSection(id: ApplicationId, sectionNumber: Int) = Action.async { implicit request =>
    applications.deleteSection(id, sectionNumber).map(_ => NoContent)
  }

}

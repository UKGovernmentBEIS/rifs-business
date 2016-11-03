package rifs.business.controllers

import javax.inject.Inject

import org.joda.time.LocalDateTime
import play.api.cache.Cached
import play.api.libs.json.JsObject
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

  def section(id: ApplicationId, sectionNumber: Int) =
    Action.async(applications.fetchSection(id, sectionNumber).map(jsonResult(_)))

  def saveSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body).map(_ => NoContent)
  }

  def completeSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body, Some(LocalDateTime.now())).map(_ => NoContent)
  }
}

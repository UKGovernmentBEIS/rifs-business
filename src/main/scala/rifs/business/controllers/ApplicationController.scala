package rifs.business.controllers

import javax.inject.Inject

import org.joda.time.LocalDateTime
import play.api.Logger
import play.api.cache.Cached
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormId, ApplicationId}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ApplicationController @Inject()(val cached: Cached, applications: ApplicationOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {
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

  def saveSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body).map(_ => NoContent)
  }

  def putSectionItem(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    def hasItemNumber(o: JsObject, num: Int) = o \ "itemNumber" match {
      case JsDefined(JsNumber(n)) if n == num => true
      case _ => false
    }

    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
        Logger.debug(app.toString)
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))
        val items = doc \ "items" match {
          case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
          case _ => Seq()
        }
        val remainingItems = items.filterNot(o => hasItemNumber(o, itemNumber))
        val newItem = request.body + ("itemNumber" -> JsNumber(itemNumber))
        val updated = doc + ("items" -> JsArray(newItem +: remainingItems))
        applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)

      case None => Future.successful(NotFound)
    }
  }

  def postSectionItem(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))

        val items: Seq[JsValue] = doc \ "items" match {
          case JsDefined(JsArray(is)) => is
          case _ => Seq()
        }

        allocateItemNumber().flatMap { n =>
          val newItem = request.body + ("itemNumber" -> JsNumber(n))
          val updated = doc + ("items" -> JsArray(newItem +: items))

          applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)
        }

      case None => Future.successful(NotFound)
    }
  }

  def allocateItemNumber(): Future[Int] = Future {
    Random.nextInt().abs
  }

  def completeSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) {
    implicit request =>
      applications.saveSection(id, sectionNumber, request.body, Some(LocalDateTime.now())).map(_ => NoContent)
  }
}

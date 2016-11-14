package rifs.business.controllers

import javax.inject.Inject

import org.joda.time.{DateTimeZone, LocalDateTime}
import play.api.cache.Cached
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import play.api.{Configuration, Logger}
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormId, ApplicationId}
import rifs.business.notifications.{NotificationService, Notifications}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ApplicationController @Inject()(val cached: Cached, applications: ApplicationOps, notifications: NotificationService, config: Configuration)
                                     (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {
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

  def delete(id: ApplicationId) = Action.async { implicit request =>
    applications.delete(id).map(_ => NoContent)
  }

  def deleteAll() = Action.async { implicit request =>
    applications.deleteAll.map(_ => NoContent)
  }

  def section(id: ApplicationId, sectionNumber: Int) =
    Action.async(applications.fetchSection(id, sectionNumber).map(jsonResult(_)))

  def sections(id: ApplicationId) =
    Action.async(applications.fetchSections(id).map(os => Ok(Json.toJson(os))))

  def saveSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body).map(_ => NoContent)
  }

  def clearSectionCompletedDate(id: ApplicationId, sectionNumber: Int) = Action.async { implicit request =>
    applications.clearSectionCompletedDate(id, sectionNumber).map { count =>
      if (count > 0) NoContent else NotFound
    }
  }

  def hasItemNumber(o: JsObject, num: Int) = o \ "itemNumber" match {
    case JsDefined(JsNumber(n)) if n == num => true
    case _ => false
  }

  def deleteSectionItem(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async { implicit request =>
    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))
        val items = doc \ "items" match {
          case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
          case _ => Seq()
        }
        val remainingItems = items.filterNot(o => hasItemNumber(o, itemNumber))
        val updated = doc + ("items" -> JsArray(remainingItems))
        applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)

      case None => Future.successful(NotFound)
    }
  }

  def getSectionItem(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async { implicit request =>
    applications.fetchAppWithSection(id, sectionNumber).map {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))
        val items = doc \ "items" match {
          case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
          case _ => Seq()
        }
        jsonResult(items.find(o => hasItemNumber(o, itemNumber)))

      case None => NotFound
    }
  }

  def putSectionItem(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    def hasItemNumber(o: JsObject, num: Int) = o \ "itemNumber" match {
      case JsDefined(JsNumber(n)) if n == num => true
      case _ => false
    }

    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
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

  /**
    * TODO: use a db sequence for this
    *
    * @return
    */
  def allocateItemNumber(): Future[Int] = Future {
    Random.nextInt().abs
  }

  def completeSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.saveSection(id, sectionNumber, request.body, Some(LocalDateTime.now(DateTimeZone.UTC))).map(_ => NoContent)
  }

  def deleteSection(id: ApplicationId, sectionNumber: Int) = Action.async { implicit request =>
    applications.deleteSection(id, sectionNumber).map(_ => NoContent)
  }

  val RIFS_EMAIL = "rifs.email"
  val RIFS_DUMMY_APPLICANT_EMAIL = s"$RIFS_EMAIL.dummyapplicant"
  val RIFS_REPLY_TO_EMAIL = s"$RIFS_EMAIL.replyto"

  def submit(id: ApplicationId) = Action.async { _ =>
    applications.submit(id).flatMap {
      case Some(submissionRef) =>
        val res = JsObject(Seq("applicationRef" -> Json.toJson(submissionRef)))
        val from = config.underlying.getString(RIFS_REPLY_TO_EMAIL)
        val to = config.underlying.getString(RIFS_DUMMY_APPLICANT_EMAIL)

        notifications.notifyPortfolioManager(submissionRef, from, to).map {
          _.map { _ => res } // this would wait for e-mail to be sent, we can just put it onto threadpool
        }.map {
          jsonResult(_)
        }.recover {
          case t =>
            Logger.error("Failed to send email on application submission", t)
            Ok(res) // we return OK, as the application was submitted
        }
      case None => Future {
        jsonResult[ApplicationId](None)
      }
    }
  }
}

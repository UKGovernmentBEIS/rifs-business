package rifs.business.controllers

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import rifs.business.Config
import rifs.business.data.{ApplicationFormOps, ApplicationOps, OpportunityOps}
import rifs.business.models.{ApplicationFormId, ApplicationId}
import rifs.business.notifications.NotificationService
import rifs.business.restmodels.{ApplicationDetail, ApplicationSectionDetail}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ApplicationController @Inject()(applications: ApplicationOps,
                                      appForms: ApplicationFormOps,
                                      opps: OpportunityOps,
                                      notifications: NotificationService)
                                     (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {
  def byId(id: ApplicationId) = Action.async(applications.byId(id).map(jsonResult(_)))

  def applicationForForm(applicationFormId: ApplicationFormId) = Action.async {
    applications.forForm(applicationFormId).map(jsonResult(_))
  }

  def application(applicationId: ApplicationId) =
    Action.async(applications.application(applicationId).map(jsonResult(_)))

  def detail(applicationId: ApplicationId) = Action.async {
    val ft = for {
      a <- OptionT(applications.application(applicationId))
      f <- OptionT(appForms.byId(a.applicationFormId))
      o <- OptionT(opps.opportunity(f.opportunityId))
    } yield {
      ApplicationDetail(a.id, a.personalReference, f.sections.length, a.sections.count(_.completedAt.isDefined), o.summary, f, a.sections)
    }

    ft.value.map(jsonResult(_))
  }

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

  def saveSection(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
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

  def deleteSection(id: ApplicationId, sectionNumber: Int) = Action.async { implicit request =>
    applications.deleteSection(id, sectionNumber).map(_ => NoContent)
  }


  def submit(id: ApplicationId) = Action.async { _ =>
    import Config.config.rifs.{email => emailConfig}

    applications.submit(id).flatMap {
      case Some(submissionRef) =>
        val from = emailConfig.replyto
        val to = emailConfig.dummyapplicant
        val mgrEmail = emailConfig.dummymanager

        val fs = Seq(
          ("Manager", notifications.notifyPortfolioManager(submissionRef, from, to)),
          ("Applicant", notifications.notifyApplicant(submissionRef, DateTime.now(DateTimeZone.UTC), from, to, mgrEmail))
        ).map {
          case (who, f) => f.recover { case t =>
            Logger.error(s"Failed to send email to $who on an application submission", t)
            None
          }
        }

        Future.sequence(fs).map(_ => Ok(JsObject(Seq("applicationRef" -> Json.toJson(submissionRef)))))

      // the required app ID is not found
      case None =>
        Logger.warn(s"An attempt to submit a non-existent application $id")
        Future.successful(jsonResult[ApplicationId](None))
    }
  }

  def savePersonalRef(id: ApplicationId) = Action.async(parse.json[JsString]) {implicit request =>
      val newVal = request.body.as[String] match {
        case "" => None
        case s  => Some(s)
      }
      applications.updatePersonalReference(id, newVal).map {
        case 0  => NotFound
        case _  => NoContent
      }
  }
}

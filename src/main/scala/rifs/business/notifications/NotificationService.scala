package rifs.business.notifications

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import com.google.inject.ImplementedBy
import play.api.libs.json.{JsObject, JsString, JsValue, Writes}
import rifs.business.data.{ApplicationFormOps, ApplicationOps, OpportunityOps}
import rifs.business.models.{ApplicationFormRow, ApplicationId, OpportunityRow}
import rifs.business.restmodels.ApplicationForm

import scala.concurrent.{ExecutionContext, Future, Promise}

object Notifications {

  sealed trait ApplicationEvent

  case object ApplicationSubmitted extends ApplicationEvent

  trait NotificationId {
    def id: String
  }

  implicit val NotificationIDJSon = new Writes[Notifications.NotificationId] {
    override def writes(o: Notifications.NotificationId): JsValue = JsObject(Seq(("id", JsString(o.id))))
  }

  case class EmailId(id: String) extends NotificationId
}

@ImplementedBy(classOf[EmailNotifications])
trait NotificationService {
  import Notifications._

  def notifyPortfolioManager(applicationFormId: ApplicationId, event: ApplicationEvent): Future[Option[NotificationId]]
}

class EmailNotifications @Inject()(mailerClient: play.api.libs.mailer.MailerClient,
                                   applicationOps: ApplicationOps,
                                   configuration: play.api.Configuration)(implicit ec: ExecutionContext) extends NotificationService {

  import Notifications._
  import play.api.libs.mailer._

  val RIFS_EMAIL = "rifs.email"
  val RIFS_DUMMY_APPLICANT_EMAIL = s"$RIFS_EMAIL.dummyapplicant"
  val RIFS_REPLY_TO_EMAIL = s"$RIFS_EMAIL.replyto"

  override def notifyPortfolioManager(applicationId: ApplicationId, event: ApplicationEvent): Future[Option[EmailId]] = {

    def createEmail(appForm: ApplicationFormRow, opportunity: OpportunityRow) = {

      val emailSubject = "Application submitted"
      val applicantEMail = configuration.underlying.getString(RIFS_DUMMY_APPLICANT_EMAIL)
      val applicantLastName = "?"
      val applicantFirstName = "Eric"

      val text = emails.txt.applicationSubmittedToPortfolioMgr(
        portFolioMgrName = "Portfolio",
        applicantTitle = "Mr",
        applicantLastName,
        applicantFirstName,
        applicantOrg = "Association of Medical Research Charities",
        applicationRefNum = appForm.id.id.toString,
        opportunityRefNumber = appForm.opportunityId.id.toString,
        opportunityTitle = opportunity.title,
        submissionLink = "http://todo.link"
      )

      Email(
        subject = emailSubject,
        from = configuration.underlying.getString(RIFS_REPLY_TO_EMAIL),
        to = Seq(s"$applicantFirstName $applicantLastName <$applicantEMail>"),
        // adds attachment
        attachments = Nil,
        // sends text, HTML or both...
        bodyText = Some(text.body),
        bodyHtml = None
      )
    }

    applicationOps.gatherDetails(applicationId).map {
      _.map(d => EmailId(mailerClient.send(createEmail(d.form, d.opp))))
    }
  }
}
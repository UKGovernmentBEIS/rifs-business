package rifs.business.notifications

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import com.google.inject.ImplementedBy
import play.api.libs.json.{JsObject, JsString, JsValue, Writes}
import rifs.business.data.{ ApplicationOps}
import rifs.business.models.{ApplicationFormRow, ApplicationId, OpportunityRow}

import scala.concurrent.{ExecutionContext, Future}

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

  def notifyApplicant(applicationFormId: ApplicationId, event: ApplicationEvent): Future[Option[NotificationId]]
}

class EmailNotifications @Inject()(mailerClient: play.api.libs.mailer.MailerClient,
                                   applicationOps: ApplicationOps,
                                   configuration: play.api.Configuration)(implicit ec: ExecutionContext) extends NotificationService {

  import Notifications._
  import play.api.libs.mailer._

  val RIFS_EMAIL = "rifs.email"
  val RIFS_DUMMY_APPLICANT_EMAIL = s"$RIFS_EMAIL.dummyapplicant"
  val RIFS_DUMMY_MANAGER_EMAIL = s"$RIFS_EMAIL.dummymanager"
  val RIFS_REPLY_TO_EMAIL = s"$RIFS_EMAIL.replyto"

  override def notifyApplicant(applicationId: ApplicationId, event: ApplicationEvent): Future[Option[EmailId]] = {

    import org.joda.time.LocalDateTime

    def createEmail(appForm: ApplicationFormRow, opportunity: OpportunityRow,
                    applicationTitle: String,
                    reviewDeadline: LocalDateTime) = {

      val emailSubject = "Application submitted"
      val applicantEMail = configuration.underlying.getString(RIFS_DUMMY_APPLICANT_EMAIL)
      val portFolioMgrEmail = configuration.underlying.getString(RIFS_DUMMY_MANAGER_EMAIL)
      val applicantLastName = "Ericsson"
      val applicantFirstName = "Eric"

      val text = emails.txt.applicationSubmittedToApplicant(
        applicantTitle = "Mr",
        applicantLastName,
        applicationTitle,
        opportunityRefNumber = opportunity.id.id.toString,
        opportunityTitle = opportunity.title,
        submissionLink = "http://todo.link",
        portFolioMgrName = "Portfolio Peter",
        portFolioMgrEmail,
        portFolioMgrPhone = "01896 000000",
        reviewDeadline
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

    (for {
      appSection <- OptionT(applicationOps.fetchSection(applicationId, rifs.business.models.APP_TITLE_SECTION_NO))
      details <-  OptionT( applicationOps.gatherDetails(applicationId) )
    } yield {
      val title = appSection.answers.value.get("title").map(_.toString).getOrElse("")
      val reviewDeadline = LocalDateTime.now //TODO: where do we get that from?
      EmailId(mailerClient.send(createEmail(details.form, details.opp, title, reviewDeadline)))
    }).value

  }

  override def notifyPortfolioManager(applicationId: ApplicationId, event: ApplicationEvent): Future[Option[EmailId]] = {

    def createEmail(appForm: ApplicationFormRow, opportunity: OpportunityRow) = {

      val emailSubject = "Application submitted"
      val managerEMail = configuration.underlying.getString(RIFS_DUMMY_MANAGER_EMAIL)
      val portFolioMgrName = "Peter Portfolio"

      val text = emails.txt.applicationSubmittedToPortfolioMgr(
        portFolioMgrName,
        applicantTitle = "Mr",
        applicantLastName = "Ericsson",
        applicantFirstName = "Eric",
        applicantOrg = "Association of Medical Research Charities",
        applicationRefNum = appForm.id.id.toString,
        opportunityRefNumber = appForm.opportunityId.id.toString,
        opportunityTitle = opportunity.title,
        submissionLink = "http://todo.link"
      )

      Email(
        subject = emailSubject,
        from = configuration.underlying.getString(RIFS_REPLY_TO_EMAIL),
        to = Seq(s"$portFolioMgrName <$managerEMail>"),
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
package rifs.business.notifications

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import com.google.inject.ImplementedBy
import org.joda.time.DateTime
import play.api.libs.mailer.{MailerClient}
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormRow, ApplicationId, OpportunityRow}

import scala.concurrent.{ExecutionContext, Future}

object Notifications {
  trait NotificationId {
    def id: String
  }

  case class EmailId(id: String) extends NotificationId
}

@ImplementedBy(classOf[EmailNotifications])
trait NotificationService {

  import Notifications._

  def notifyPortfolioManager(applicationFormId: ApplicationId, from: String, to: String): Future[Option[NotificationId]]
  def notifyApplicant(applicationFormId: ApplicationId, submittedAt: DateTime, from: String, to: String, mgrEmail: String): Future[Option[NotificationId]]
}

class EmailNotifications @Inject()(sender: MailerClient, applications: ApplicationOps)(implicit ec: ExecutionContext) extends NotificationService {

  import Notifications._
  import play.api.libs.mailer._

  override def notifyApplicant(applicationId: ApplicationId, submittedAt: DateTime, from: String, to: String, mgrEmail: String): Future[Option[EmailId]] = {

    def createEmail(appForm: ApplicationFormRow, opportunity: OpportunityRow,
                    applicationTitle: String,
                    reviewDeadline: DateTime) = {

      val emailSubject = "Application submitted"
      val applicantEMail = to
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
        mgrEmail,
        portFolioMgrPhone = "01896 000000",
        reviewDeadline
      )

      Email(
        subject = emailSubject,
        from,
        to = Seq(s"$applicantFirstName $applicantLastName <$applicantEMail>"),
        // adds attachment
        attachments = Nil,
        // sends text, HTML or both...
        bodyText = Some(text.body),
        bodyHtml = None
      )
    }

    (for {
      appSection <- OptionT(applications.fetchSection(applicationId, rifs.business.models.APP_TITLE_SECTION_NO))
      details <-  OptionT( applications.gatherDetails(applicationId) )
    } yield {
      val title = appSection.answers.value.get("title").map(_.toString).getOrElse("")
      val reviewDeadline = submittedAt.plusDays(rifs.business.models.APP_REVIEW_TIME_DAYS)
      EmailId(sender.send(createEmail(details.form, details.opp, title, reviewDeadline)))
    }).value

  }

  override def notifyPortfolioManager(applicationId: ApplicationId, from: String, to: String): Future[Option[EmailId]] = {

    def createEmail(appForm: ApplicationFormRow, opportunity: OpportunityRow) = {

      val emailSubject = "Application submitted"
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
        from,
        to = Seq(s"$portFolioMgrName <$to>"),
        // adds attachment
        attachments = Nil,
        // sends text, HTML or both...
        bodyText = Some(text.body),
        bodyHtml = None
      )
    }

    applications.gatherDetails(applicationId).map {
      _.map(d => EmailId(sender.send(createEmail(d.form, d.opp))))
    }
  }
}
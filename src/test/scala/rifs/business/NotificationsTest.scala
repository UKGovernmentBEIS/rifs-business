package rifs.business

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.mailer.{Email, MailerClient}
import rifs.business.data.ApplicationDetails
import rifs.business.models._
import rifs.business.notifications.EmailNotifications

import scala.concurrent.{ExecutionContext, Future}

class NotificationsTest extends WordSpecLike with Matchers with OptionValues with ScalaFutures {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  import NotificationsTestData._

  "notification" should {
    "return no notification ID for a missing application ID" in {
      val notification = new EmailNotifications(new DummyMailer(""), new DummyGatherDetails(Future.successful(None)))

      val res = notification.notifyPortfolioManager(APP_ID, "from", "to")
      res.futureValue shouldBe None
    }

    "create a notification ID upon success" in {
      val MAIL_ID = "yey"
      val sender = new DummyMailer(MAIL_ID)

      val notification = new EmailNotifications(sender, appOps)
      val res = notification.notifyPortfolioManager(APP_ID, "from", "to")
      res.futureValue.value.id shouldBe MAIL_ID
    }

    "return error if e-mailer throws" in {
      val sender = new DummyMailer(throw new RuntimeException())

      val notification = new EmailNotifications(sender, appOps)
      val res = notification.notifyPortfolioManager(APP_ID, "from", "to")
      whenReady(res.failed) { ex => ex shouldBe a[RuntimeException] }
    }
  }
}

object NotificationsTestData {
  val APP_ID = ApplicationId(1)

  class DummyMailer(result: => String) extends MailerClient {
    override def send(email: Email): String = result
  }

  val appOps = {
    val APP_FORM_ID = ApplicationFormId(1)
    val OPPORTUNITY_ID = OpportunityId(1)
    val opp = OpportunityRow(OPPORTUNITY_ID, "oz1", "", None, None, 0, "")

    val appDetails = ApplicationDetails(
      ApplicationRow(Some(APP_ID), APP_FORM_ID),
      ApplicationFormRow(APP_FORM_ID, OPPORTUNITY_ID), opp)

    new DummyGatherDetails(Future.successful(Some(appDetails)))
  }

  class DummyGatherDetails(result: => Future[Option[ApplicationDetails]]) extends StubApplicationOps {
    override def gatherDetails(id: SubmittedApplicationRef): Future[Option[ApplicationDetails]] = result
  }

}

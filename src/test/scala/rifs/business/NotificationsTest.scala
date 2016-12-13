package rifs.business

import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import play.api.libs.json.{JsObject, JsString}
import play.api.libs.mailer.{Email, MailerClient}
import rifs.business.data.{ApplicationDetails, OpportunityOps}
import rifs.business.models._
import rifs.business.notifications.EmailNotifications
import rifs.business.restmodels.{Opportunity, OpportunitySummary}

import scala.concurrent.{ExecutionContext, Future}

class NotificationsTest extends WordSpecLike with Matchers with OptionValues with ScalaFutures {
  implicit val waitConf = PatienceConfig(Span(3, Seconds))

  import NotificationsTestData._

  "notification" should {

    "return no notification ID for a missing application ID" in {
      val notification = new EmailNotifications(new DummyMailer(""), new DummyGatherDetailsAndSect(Future.successful(None), Future.successful(None)), OppNotFoundOps)

      val res1 = notification.notifyPortfolioManager(APP_ID, "from", "to")
      res1.futureValue shouldBe None

      val res2 = notification.notifyApplicant(APP_ID, DateTime.now(DateTimeZone.UTC), "from", "to", "mgr@")
      res2.futureValue shouldBe None
    }

    "return no notification ID for a missing opportunity ID" in {
      val notification = new EmailNotifications(new DummyMailer(""), new DummyGatherDetailsAndSect(Future.successful(None), Future.successful(None)), OppNotFoundOps)
      val res1 = notification.notifyManager(OpportunityId(123), "from", "to")
      res1.futureValue shouldBe None
    }

    "return no notification ID for missing details section" in {
      val notification = new EmailNotifications(new DummyMailer(""),
        new DummyGatherDetailsAndSect(appOps.gatherDetails(APP_ID), Future.successful(None)),
        OppNotFoundOps)
      val res = notification.notifyApplicant(APP_ID, DateTime.now(DateTimeZone.UTC), "from", "to", "mgr@")
      res.futureValue shouldBe None
    }

    "create a notification ID upon success" in {
      val MAIL_ID = "yey"
      val sender = new DummyMailer(MAIL_ID)

      val notificationMgr = new EmailNotifications(sender, appOps, OppNotFoundOps)
      val res1 = notificationMgr.notifyPortfolioManager(APP_ID, "from", "to")
      res1.futureValue.value.id shouldBe MAIL_ID

      val notificationAppl = new EmailNotifications(sender, appOpsAndSection, OppNotFoundOps)
      val res2 = notificationAppl.notifyApplicant(APP_ID, DateTime.now(DateTimeZone.UTC), "from@", "to@", "mgr@")
      res2.futureValue.value.id shouldBe MAIL_ID

      val oppNotify = new EmailNotifications(sender, appOps, OppOps)
      val res3 = oppNotify.notifyManager(OPP_ID, "from@", "to@")
      res3.futureValue.value.id shouldBe MAIL_ID
    }

    "return error if e-mailer throws" in {
      val sender = new DummyMailer(throw new RuntimeException())

      val notificationMgr = new EmailNotifications(sender, appOps, OppNotFoundOps)
      val res1 = notificationMgr.notifyPortfolioManager(APP_ID, "from", "to")
      whenReady(res1.failed) { ex => ex shouldBe a[RuntimeException] }

      val notificationAppl = new EmailNotifications(sender, appOpsAndSection, OppNotFoundOps)
      val res2 = notificationAppl.notifyApplicant(APP_ID, DateTime.now(DateTimeZone.UTC), "from@", "to@", "mgr@")
      whenReady(res2.failed) { ex => ex shouldBe a[RuntimeException] }

      val oppNotify = new EmailNotifications(sender, appOps, OppOps)
      val res3 = oppNotify.notifyManager(OPP_ID, "from@", "to@")
      whenReady(res3.failed) { ex => ex shouldBe a[RuntimeException] }
    }
  }
}

object NotificationsTestData {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val APP_ID = ApplicationId(1)
  val OppNotFoundOps = new DummyOpportunityOps(_ =>Future.successful(None))

  val OPP_ID = OpportunityId(1399)
  val OppOps = {
    new DummyOpportunityOps( {oid=>
      Future{ if ( oid == OPP_ID ) Some(OpportunityRow(oid, "Op1", "today", None, 2000, "£", None, None)) else None }
    } )
  }

  class DummyMailer(result: => String) extends MailerClient {
    override def send(email: Email): String = result
  }

  val (appOpsAndSection, appOps) = {
    val appFormId = ApplicationFormId(1)
    val oppId = OpportunityId(1)
    val opp = OpportunityRow(oppId, "oz1", "", None, 0, "", None, None)

    val appDetails = ApplicationDetails(
      ApplicationRow(APP_ID, appFormId, None),
      ApplicationFormRow(appFormId, oppId), opp)

    val details = Future.successful(Some(appDetails))
    val appSectRow = ApplicationSectionRow(ApplicationSectionId(0), APP_ID, rifs.business.models.APP_TITLE_SECTION_NO,
      JsObject(Seq("title"->JsString("app title"))), None)
    (new DummyGatherDetailsAndSect(details, Future.successful(Some(appSectRow))), new DummyGatherDetails(details))
  }

  class DummyGatherDetails(result: => Future[Option[ApplicationDetails]]) extends StubApplicationOps {
    override def gatherDetails(id: SubmittedApplicationRef): Future[Option[ApplicationDetails]] = result
  }

  class DummyGatherDetailsAndSect(details: => Future[Option[ApplicationDetails]],
                                  section: => Future[Option[ApplicationSectionRow]]) extends DummyGatherDetails(details) {
    override def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]] = section
  }


  class DummyOpportunityOps(oppRow: OpportunityId => Future[Option[OpportunityRow]]) extends OpportunityOps {

    override def byId(id: OpportunityId): Future[Option[OpportunityRow]] = oppRow(id)

    override def opportunity(opportunityId: OpportunityId): Future[Option[Opportunity]] = ???

    override def findOpen: Future[Set[Opportunity]] = ???

    override def summaries: Future[Set[Opportunity]] = ???

    override def openSummaries: Future[Set[Opportunity]] = ???

    override def updateSummary(summary: OpportunitySummary): Future[Int] = ???

    override def publish(id: OpportunityId): Future[Option[DateTime]] = ???

    override def duplicate(id: OpportunityId): Future[Option[OpportunityId]] = ???

    override def saveSectionDescription(id: OpportunityId, sectionNo: Int, description: Option[String]): Future[Int] = ???

    override def reset(): Future[Unit] = ???
  }
}

/*
 * Copyright (C) 2016  Department for Business, Energy and Industrial Strategy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rifs.business

import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import play.api.libs.json.{JsObject, JsString}
import play.api.libs.mailer.{Email, MailerClient}
import rifs.business.data.ApplicationDetails
import rifs.business.models._
import rifs.business.notifications.EmailNotifications

import scala.concurrent.{ExecutionContext, Future}

class NotificationsTest extends WordSpecLike with Matchers with OptionValues with ScalaFutures {
  implicit val waitConf = PatienceConfig(Span(3, Seconds))

  import NotificationsTestData._

  "notification" should {

    "return no notification ID for a missing application ID" in {
      val notification = new EmailNotifications(new DummyMailer(""), new DummyGatherDetailsAndSect(Future.successful(None), Future.successful(None)), oppNotFoundOps)

      val res1 = notification.notifyPortfolioManager(dummyAppId, "from", "to")
      res1.futureValue shouldBe None

      val res2 = notification.notifyApplicant(dummyAppId, DateTime.now(DateTimeZone.UTC), "from", "to", "mgr@")
      res2.futureValue shouldBe None
    }

    "return no notification ID for a missing opportunity ID" in {
      val notification = new EmailNotifications(new DummyMailer(""), new DummyGatherDetailsAndSect(Future.successful(None), Future.successful(None)), oppNotFoundOps)
      val res1 = notification.notifyManager(OpportunityId(123), "from", "to")
      res1.futureValue shouldBe None
    }

    "return no notification ID for missing details section" in {
      val notification = new EmailNotifications(new DummyMailer(""),
        new DummyGatherDetailsAndSect(appOps.gatherDetails(dummyAppId), Future.successful(None)),
        oppNotFoundOps)
      val res = notification.notifyApplicant(dummyAppId, DateTime.now(DateTimeZone.UTC), "from", "to", "mgr@")
      res.futureValue shouldBe None
    }

    "create a notification ID upon success" in {
      val MAIL_ID = "yey"
      val sender = new DummyMailer(MAIL_ID)

      val notificationMgr = new EmailNotifications(sender, appOps, oppNotFoundOps)
      val res1 = notificationMgr.notifyPortfolioManager(dummyAppId, "from", "to")
      res1.futureValue.value.id shouldBe MAIL_ID

      val notificationAppl = new EmailNotifications(sender, appOpsAndSection, oppNotFoundOps)
      val res2 = notificationAppl.notifyApplicant(dummyAppId, DateTime.now(DateTimeZone.UTC), "from@", "to@", "mgr@")
      res2.futureValue.value.id shouldBe MAIL_ID

      val oppNotify = new EmailNotifications(sender, appOps, oppOps)
      val res3 = oppNotify.notifyManager(dummyOppId, "from@", "to@")
      res3.futureValue.value.id shouldBe MAIL_ID
    }

    "return error if e-mailer throws" in {
      val sender = new DummyMailer(throw new RuntimeException())

      val notificationMgr = new EmailNotifications(sender, appOps, oppNotFoundOps)
      val res1 = notificationMgr.notifyPortfolioManager(dummyAppId, "from", "to")
      whenReady(res1.failed) { ex => ex shouldBe a[RuntimeException] }

      val notificationAppl = new EmailNotifications(sender, appOpsAndSection, oppNotFoundOps)
      val res2 = notificationAppl.notifyApplicant(dummyAppId, DateTime.now(DateTimeZone.UTC), "from@", "to@", "mgr@")
      whenReady(res2.failed) { ex => ex shouldBe a[RuntimeException] }

      val oppNotify = new EmailNotifications(sender, appOps, oppOps)
      val res3 = oppNotify.notifyManager(dummyOppId, "from@", "to@")
      whenReady(res3.failed) { ex => ex shouldBe a[RuntimeException] }
    }
  }
}

object NotificationsTestData {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val dummyAppId = ApplicationId(1)
  val oppNotFoundOps = new DummySaveDescription(_ => Future.successful(None))

  val dummyOppId = OpportunityId(1399)
  val dummyOpp = OpportunityRow(dummyOppId, "Op1", "today", None, 2000, "per event", None, None)
  val oppOps = new DummySaveDescription(oid => Future.successful(if (oid == dummyOppId) Some(dummyOpp) else None))

  class DummyMailer(result: => String) extends MailerClient {
    override def send(email: Email): String = result
  }

  val (appOpsAndSection, appOps) = {
    val appFormId = ApplicationFormId(1)
    val oppId = OpportunityId(1)
    val opp = OpportunityRow(oppId, "oz1", "", None, 0, "", None, None)

    val appDetails = ApplicationDetails(
      ApplicationRow(dummyAppId, appFormId, None),
      ApplicationFormRow(appFormId, oppId), opp)

    val details = Future.successful(Some(appDetails))
    val appSectRow = ApplicationSectionRow(ApplicationSectionId(0), dummyAppId, rifs.business.models.APP_TITLE_SECTION_NO,
      JsObject(Seq("title" -> JsString("app title"))), None)
    (new DummyGatherDetailsAndSect(details, Future.successful(Some(appSectRow))), new DummyGatherDetails(details))
  }

  class DummyGatherDetails(result: => Future[Option[ApplicationDetails]]) extends StubApplicationOps {
    override def gatherDetails(id: SubmittedApplicationRef): Future[Option[ApplicationDetails]] = result
  }

  class DummyGatherDetailsAndSect(details: => Future[Option[ApplicationDetails]],
                                  section: => Future[Option[ApplicationSectionRow]]) extends DummyGatherDetails(details) {
    override def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]] = section
  }

  class DummySaveDescription(oppRow: OpportunityId => Future[Option[OpportunityRow]]) extends StubOpportunityOps {
    override def byId(id: OpportunityId): Future[Option[OpportunityRow]] = oppRow(id)
  }

}


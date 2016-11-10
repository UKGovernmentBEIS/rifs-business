package rifs.business

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.db.evolutions.ApplicationEvolutions
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.mailer.{Email, MailerClient}
import rifs.business.data.{ApplicationDetails, ApplicationOps}
import rifs.business.models._
import rifs.business.notifications.{EmailNotifications, Notifications}

import scala.concurrent.Future

class NotificationsTest extends WordSpecLike with Matchers with OptionValues with ScalaFutures {

  import org.mockito.{ArgumentMatchers, Mockito}

  "notification" should {
    val APP_ID = ApplicationId(1)
    val appOps = mock.MockitoSugar.mock[ApplicationOps]
    val evolAPi = mock.MockitoSugar.mock[ApplicationEvolutions]
    val mailerClient = mock.MockitoSugar.mock[MailerClient]

    val application = new GuiceApplicationBuilder()
      .overrides(bind[ApplicationOps].to(appOps))
      .overrides(bind[ApplicationEvolutions].to(evolAPi))
      .overrides(bind[MailerClient].to(mailerClient))
      .build()

    val notification = application.injector.instanceOf[EmailNotifications]

    "return empty notification ID for a missing application ID" in {
      Mockito.when(appOps.gatherDetails(APP_ID)).thenReturn(Future.successful(None))
      val res = notification.notifyPortfolioManager(APP_ID, Notifications.ApplicationSubmitted)
      res.futureValue shouldBe None
    }

    def setupMailer() = {
      val APP_FORM_ID = ApplicationFormId(1)
      val OPPORTUNITY_ID = OpportunityId(1)
      Mockito.when(appOps.gatherDetails(APP_ID)).thenReturn(Future.successful(Some(
        ApplicationDetails(ApplicationRow(Some(APP_ID), APP_FORM_ID),
          ApplicationFormRow(APP_FORM_ID, OPPORTUNITY_ID),
          OpportunityRow(OPPORTUNITY_ID, "oz1", "", None, None, 0, "")
        ))))
    }

    "create a notification ID upon success" in {
      val MAIL_ID = "yey"
      setupMailer()
      Mockito.when(mailerClient.send(ArgumentMatchers.any[Email]())).thenReturn(MAIL_ID)
      val res = notification.notifyPortfolioManager(APP_ID, Notifications.ApplicationSubmitted)
      res.futureValue.value.id shouldBe MAIL_ID
    }

    "return error if e-mailer throws" in {
      setupMailer()
      Mockito.when(mailerClient.send(ArgumentMatchers.any[Email]())).thenThrow(classOf[RuntimeException])
      val res = notification.notifyPortfolioManager(APP_ID, Notifications.ApplicationSubmitted)
      whenReady(res.failed) { ex => ex shouldBe a[RuntimeException] }
    }
  }
}

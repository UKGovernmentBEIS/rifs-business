package rifs.business

import org.scalatest._
import play.api.db.evolutions.ApplicationEvolutions
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.mailer.{Email, MailerClient}
import rifs.business.data.{ApplicationFormOps, ApplicationOps, OpportunityOps}
import rifs.business.models._
import rifs.business.notifications.{EmailNotifications, Notifications}
import rifs.business.restmodels.ApplicationForm

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.reflect._
import scala.util.{Failure, Success}

class NotificationsTest extends WordSpecLike with Matchers with OptionValues {

  import org.mockito.{ArgumentMatchers, Mockito}

  def checkSuccessFut[T](f: Future[T])(func: T => Unit) = {
    Await.result(f, 3.seconds)
    f.value.isDefined shouldBe true
    f.value.foreach { v =>
      v.isSuccess shouldBe true
      func(v.get)
    }
  }

  def checkFailedFut[E: ClassTag](f: Future[_]) = {
    val f1 = Await.ready(f, 3.seconds)
    f1.value.isDefined shouldBe true
    f1.value.foreach { v =>
      v.isSuccess shouldBe false
      v match {
        case Failure(e) => classTag[E].runtimeClass.isAssignableFrom(e.getClass) shouldBe true
        case Success(_) => fail()
      }
    }
  }

  "notification should" {

    val APP_ID = ApplicationId(1)
    val appOps = mock.MockitoSugar.mock[ApplicationOps]
    val formOps = mock.MockitoSugar.mock[ApplicationFormOps]
    val oppOps = mock.MockitoSugar.mock[OpportunityOps]
    val evolAPi = mock.MockitoSugar.mock[ApplicationEvolutions]
    val mailerClient = mock.MockitoSugar.mock[MailerClient]

    val application = new GuiceApplicationBuilder()
      .overrides(bind[ApplicationOps].to(appOps))
      .overrides(bind[ApplicationFormOps].to(formOps))
      .overrides(bind[OpportunityOps].to(oppOps))
      .overrides(bind[ApplicationEvolutions].to(evolAPi))
      .overrides(bind[MailerClient].to(mailerClient))
      .build()

    val notification = application.injector.instanceOf[EmailNotifications]

    "return empty notification ID for a missing application ID" in {

      Mockito.when(appOps.byId(APP_ID)).thenReturn(Future.successful(None))
      val res = notification.notifyPortfolioManager(APP_ID, Notifications.ApplicationSubmitted)
      checkSuccessFut(res) {
        _ shouldBe None
      }
    }

    def setupMailer() = {
      val APP_FORM_ID = ApplicationFormId(1)
      val OPPORTUNITY_ID = OpportunityId(1)
      Mockito.when(appOps.byId(APP_ID)).thenReturn(Future.successful(Some(ApplicationRow(Some(APP_ID), APP_FORM_ID))))
      Mockito.when(formOps.byId(APP_FORM_ID)).thenReturn(Future.successful(Some(ApplicationForm(APP_FORM_ID, OPPORTUNITY_ID, Nil))))
      val opp = OpportunityRow(OPPORTUNITY_ID, "oz1", "", None, None, 0, "")
      Mockito.when(oppOps.byId(OPPORTUNITY_ID)).thenReturn(Future.successful(Some(opp)))
    }

    "create a notification ID upon success" in {
      val MAIL_ID = "yey"
      notification.synchronized {

        setupMailer()
        Mockito.when(mailerClient.send(ArgumentMatchers.any[Email]())).thenReturn(MAIL_ID)
        val res = notification.notifyPortfolioManager(APP_ID, Notifications.ApplicationSubmitted)
        checkSuccessFut(res) { nid =>
          nid.value shouldBe MAIL_ID
        }
      }
    }

    "return error if e-mailer throws" in {
      notification.synchronized {
        setupMailer()

        Mockito.when(mailerClient.send(ArgumentMatchers.any[Email]())).thenThrow(classOf[RuntimeException])
        val res = notification.notifyPortfolioManager(APP_ID, Notifications.ApplicationSubmitted)
        checkFailedFut[RuntimeException](res)
      }
    }

    1
  }

}

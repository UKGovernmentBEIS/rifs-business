package rifs.business.forms

import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpecLike}

class package$Test extends WordSpecLike with Matchers {
  val epoch = new DateTime(0L)

  "handleEvent" should {
    "accept a form and a Save event and produce a new form with the unvalidated answers" in {
      val form = Form()
      val answers = Seq(Answer("foo", "bar"))
      val event = Save(answers, epoch)

      val expected = Form(unvalidatedAnswers = Some(answers))
      handleEvent(form, Seq(), event) shouldBe expected
    }

    "accept a form and a Preview event and produce a new form with the preview answers" in {
      val form = Form()
      val answers = Seq(Answer("foo", "bar"))
      val event = Preview(answers, epoch)

      val expected = Form(previewAnswers = Some(answers))
      handleEvent(form, Seq(), event) shouldBe expected
    }

    case object FormFail extends FormRule {
      override def check(form: Form, answers: Seq[Answer]): Seq[Error] = Seq(FormError("fail"))
    }
    case object FormPass extends FormRule {
      override def check(form: Form, answers: Seq[Answer]): Seq[Error] = Seq()
    }

    "accept a form and a Complete event where the rules fail and produce a new form with the answers in unvalidatedAnswers" in {
      val rules = Seq(FormFail)
      val form = Form()
      val answers = Seq(Answer("foo", "bar"))
      val event = Validate(answers, epoch)

      handleEvent(form, rules, event) shouldBe Form(errors = Seq(FormError("fail")), unvalidatedAnswers = Some(answers))
    }

    "accept a form and a Complete event where the rules pass and produce a new form the answers in validatedAnswers" in {
      val rules = Seq(FormPass)
      val form = Form()
      val answers = Seq(Answer("foo", "bar"))
      val event = Validate(answers, epoch)

      handleEvent(form, rules, event) shouldBe Form(validatedAnswers = Some(answers))
    }
  }

}

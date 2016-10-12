package rifs.business.forms

import org.scalatest.{Matchers, WordSpecLike}

class package$Test extends WordSpecLike with Matchers {

  "handleEvent" should {
    "accept a form and a Save event and produce a new form with a state of InProgress and the unvalidated answers" in {
      val form = Form(NotStarted)
      val answers = Seq(Answer("foo", "bar"))
      val event = Save(answers)

      val expected = Form(InProgress, unvalidatedAnswers = answers)
      Seq(NotStarted, InProgress, Complete).foreach { state =>
        handleEvent(form.copy(state = state), event) shouldBe expected
      }
    }

    "accept a form and a Preview event and produce a new form with the preview answers an unchanged state" in {
      val form = Form(NotStarted)
      val answers = Seq(Answer("foo", "bar"))
      val event = Preview(answers)

      val expected = Form(NotStarted, previewAnswers = answers)
      Seq(NotStarted, InProgress, Complete).foreach { state =>
        handleEvent(form.copy(state = state), event) shouldBe expected.copy(state = state)
      }
    }

    case object FormFail extends FormRule {
      override def check(form: Form, answers: Seq[Answer]): Boolean = false
    }
    case object FormPass extends FormRule {
      override def check(form: Form, answers: Seq[Answer]): Boolean = true
    }

    "accept a form and a Complete event where the rules fail and produce a new form with state InProgress and the answers in unvalidatedAnswers" in {
      val rules = Seq(FormFail)
      val form = Form(Complete, formRules = rules)
      val answers = Seq(Answer("foo", "bar"))
      val event = MarkComplete(answers)

      handleEvent(form, event) shouldBe Form(InProgress, formRules = rules, unvalidatedAnswers = answers)
    }

    "accept a form and a Complete event where the rules pass and produce a new form with state InProgress and the answers in validatedAnswers" in {
      val rules = Seq(FormPass)
      val form = Form(Complete, formRules = rules)
      val answers = Seq(Answer("foo", "bar"))
      val event = MarkComplete(answers)

      handleEvent(form, event) shouldBe Form(Complete, formRules = rules, validatedAnswers = answers)
    }
  }

}

package rifs.business.forms

import cats.data.NonEmptyList
import org.scalatest.{Matchers, WordSpecLike}

class package$Test extends WordSpecLike with Matchers {
  "handleEvent" should {
    "accept a Save event and produce an Unvalidated with the answers" in {
      val answers = Map("foo" -> "bar")
      val event = Save(answers)

      val expected = NotValidated(answers)
      handleEvent(Seq(), event) shouldBe expected
    }

    case object FormFail extends Rule {
      override def check(answers: Answers): Seq[Error] = Seq(Error(None, "fail"))
    }
    case object FormPass extends Rule {
      override def check(answers: Answers): Seq[Error] = Seq()
    }

    "accept a Complete event where the rules fail and produce an Invalid with the answers and errors" in {
      val rules = Seq(FormFail)
      val answers = Map("foo" -> "bar")
      val event = Validate(answers)

      handleEvent(rules, event) shouldBe Invalid(answers, NonEmptyList(Error(None, "fail"), List()))
    }

    "accept Complete event where the rules pass and produce a Valid with the answers" in {
      val rules = Seq(FormPass)
      val answers = Map("foo" -> "bar")
      val event = Validate(answers)

      handleEvent(rules, event) shouldBe Valid(answers)
    }
  }
}

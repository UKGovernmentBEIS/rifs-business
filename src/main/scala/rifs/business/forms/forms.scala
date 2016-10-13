package rifs.business

import cats.data.NonEmptyList

package object forms {

  trait Rule {
    def check(answers: Answers): Seq[Error]
  }

  type Answers = Map[String, String]

  case class Error(label: Option[String], text: String)

  sealed trait Event

  case class Save(answers: Answers) extends Event

  case class Validate(answers: Answers) extends Event

  def handleEvent(formRules: Seq[Rule], event: Event): ValidationState[Answers, Error] = event match {
    case Save(answers) => NotValidated(answers)

    case Validate(answers) => checkRules(formRules, answers) match {
      case Seq() => Valid(answers)
      case head :: tail => Invalid(answers, NonEmptyList(head, tail))
    }
  }

  def checkRules(formRules: Seq[Rule], answers: Answers): Seq[Error] = formRules.flatMap(_.check(answers))

}

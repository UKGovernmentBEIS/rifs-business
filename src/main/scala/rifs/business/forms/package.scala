package rifs.business

import org.joda.time.DateTime

package object forms {

  trait FormRule {
    def check(form: Form, answers: Seq[Answer]): Seq[FormError]
  }

  case class Form(
                   questions: Seq[Question] = Seq(),
                   formRules: Seq[FormRule] = Seq(),
                   errors: Seq[Error] = Seq(),
                   validatedAnswers: Option[Seq[Answer]] = None,
                   unvalidatedAnswers: Option[Seq[Answer]] = None,
                   previewAnswers: Option[Seq[Answer]] = None,
                   updatedAt: DateTime = new DateTime(0L)
                 )

  trait QuestionRule

  case class Question(label: String, rules: Seq[QuestionRule])

  case class Answer(label: String, value: String)

  sealed trait Error

  case class AnswerError(label: String, text: String) extends Error

  case class FormError(text: String) extends Error

  sealed trait Event

  case class Save(answers: Seq[Answer], at: DateTime) extends Event

  case class Validate(answers: Seq[Answer], at: DateTime) extends Event

  case class Preview(answers: Seq[Answer], at: DateTime) extends Event

  def handleEvent(form: Form, event: Event): Form = event match {
    case Save(answers, ts) => form.copy(unvalidatedAnswers = Some(answers), updatedAt = ts)
    case Preview(answers, ts) => form.copy(previewAnswers = Some(answers), updatedAt = ts)
    case Validate(answers, ts) => checkRules(form, answers) match {
      case Seq() => form.copy(errors = Seq(), validatedAnswers = Some(answers), unvalidatedAnswers = None, updatedAt = ts)
      case errors => form.copy(errors = errors, unvalidatedAnswers = Some(answers), updatedAt = ts)
    }
  }

  def checkRules(form: Form, answers: Seq[Answer]): Seq[Error] = form.formRules.flatMap(_.check(form, answers))

}

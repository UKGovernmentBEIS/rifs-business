package rifs.business

package object forms {

  trait FormRule {
    def check(form: Form, answers: Seq[Answer]): Boolean
  }

  case class Form(
                   state: FormState,
                   questions: Seq[Question] = Seq(),
                   formRules: Seq[FormRule] = Seq(),
                   validatedAnswers: Seq[Answer] = Seq(),
                   unvalidatedAnswers: Seq[Answer] = Seq(),
                   previewAnswers: Seq[Answer] = Seq())

  trait QuestionRule

  case class Question(label: String, rules: Seq[QuestionRule])

  case class Answer(label: String, value: String)

  sealed trait FormState

  case object NotStarted extends FormState

  case object InProgress extends FormState

  case object Complete extends FormState

  sealed trait Event

  case class Save(answers: Seq[Answer]) extends Event

  case class MarkComplete(answers: Seq[Answer]) extends Event

  case class Preview(answers: Seq[Answer]) extends Event

  def handleEvent(form: Form, event: Event): Form = event match {
    case Save(answers) => form.copy(state = InProgress, unvalidatedAnswers = answers)
    case Preview(answers) => form.copy(previewAnswers = answers)
    case MarkComplete(answers) =>
      if (allTestsPass(form, answers)) form.copy(state = Complete, validatedAnswers = answers, unvalidatedAnswers = Seq())
      else form.copy(state = InProgress, unvalidatedAnswers = answers)
  }

  def allTestsPass(form: Form, answers: Seq[Answer]): Boolean = form.formRules.forall(rule => rule.check(form, answers))

}

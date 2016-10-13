package rifs.business.forms

import cats.data.NonEmptyList

trait ValidationState[T, E]

case class NotValidated[T, E](values: T) extends ValidationState[T, E]

case class Valid[T, E](values: T) extends ValidationState[T, E]

case class Invalid[T, E](values: T, errors: NonEmptyList[E]) extends ValidationState[T, E]
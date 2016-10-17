package rifs.business.models

case class ResponseId(id: Long) extends AnyVal

case class ResponseRow(id: ResponseId, applicationId: ApplicationFormId)
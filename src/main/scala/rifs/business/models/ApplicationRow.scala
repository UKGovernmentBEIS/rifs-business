package rifs.business.models

import org.joda.time.LocalDateTime
import play.api.libs.json.JsObject

case class ApplicationId(id: Long)

case class ApplicationSectionId(id: Long)

case class ApplicationSectionRow(id: Option[ApplicationSectionId], applicationId: ApplicationId, sectionNumber:Int, answers: JsObject, completedAt:Option[LocalDateTime])

case class ApplicationRow(id: Option[ApplicationId], applicationFormId: ApplicationFormId)

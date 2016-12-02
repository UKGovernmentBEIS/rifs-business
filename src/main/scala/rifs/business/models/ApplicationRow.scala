package rifs.business.models

import org.joda.time.DateTime
import play.api.libs.json.JsObject

case class ApplicationId(id: Long)

case class ApplicationSectionId(id: Long)

case class ApplicationSectionRow(id: ApplicationSectionId, applicationId: ApplicationId, sectionNumber: Int, answers: JsObject, completedAt: Option[DateTime])

case class ApplicationRow(id: ApplicationId, applicationFormId: ApplicationFormId)

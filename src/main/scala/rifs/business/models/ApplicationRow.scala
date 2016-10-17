package rifs.business.models

import play.api.libs.json.JsValue

case class ApplicationId(id: Long)

case class ApplicationSectionId(id: Long)

case class ApplicationSectionRow(id: Option[ApplicationSectionId], applicationId: ApplicationId, sectionNumber:Int, answers: JsValue)

case class ApplicationRow(id: Option[ApplicationId], applicationFormId: ApplicationFormId)

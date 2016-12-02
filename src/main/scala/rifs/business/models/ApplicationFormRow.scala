package rifs.business.models

import play.api.libs.json.JsArray

case class ApplicationFormId(id: Long) extends AnyVal

case class ApplicationFormSectionId(id: Long) extends AnyVal

case class ApplicationFormQuestionId(id: Long) extends AnyVal

case class ApplicationFormSectionRow(
                                      id: Option[ApplicationFormSectionId],
                                      applicationFormId: ApplicationFormId,
                                      sectionNumber: Int,
                                      title: String,
                                      sectionType: String,
                                      fields: JsArray
                                    )

case class ApplicationFormRow(id: Option[ApplicationFormId], opportunityId: OpportunityId)

case class ApplicationFormQuestionRow(
                                       id: Option[ApplicationFormQuestionId],
                                       applicationFormSectionId: ApplicationFormSectionId,
                                       key: String,
                                       text: String,
                                       description: Option[String],
                                       helpText: Option[String])
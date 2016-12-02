package rifs.business.restmodels

import play.api.libs.json.JsArray
import rifs.business.models.{ApplicationFormId, OpportunityId}

case class Question(key: String, text: String, description: Option[String], helpText: Option[String])

case class ApplicationFormSection(
                                   sectionNumber: Int,
                                   title: String,
                                   questions: Seq[Question],
                                   sectionType: String,
                                   fields: JsArray)

case class ApplicationForm(id: ApplicationFormId, opportunityId: OpportunityId, sections: Seq[ApplicationFormSection])

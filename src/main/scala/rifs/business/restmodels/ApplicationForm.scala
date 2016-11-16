package rifs.business.restmodels

import rifs.business.models.{ApplicationFormId, OpportunityId}

case class Question(key: String, text: String, description: Option[String], helpText: Option[String])

case class ApplicationFormSection(sectionNumber: Int, title: String, questions: Seq[Question])

case class ApplicationForm(id: ApplicationFormId, opportunityId: OpportunityId, sections: Seq[ApplicationFormSection])

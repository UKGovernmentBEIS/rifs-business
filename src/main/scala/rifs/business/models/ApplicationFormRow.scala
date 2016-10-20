package rifs.business.models

case class ApplicationFormId(id: Long) extends AnyVal

case class ApplicationFormSectionId(id: Long) extends AnyVal

case class ApplicationFormSectionRow(id: ApplicationFormSectionId, applicationId: ApplicationFormId, sectionNumber: Int, title: String)

case class ApplicationFormRow(id: ApplicationFormId, opportunityId: OpportunityId)

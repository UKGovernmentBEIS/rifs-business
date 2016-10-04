package rifs.models

case class ApplicationId(id: Long) extends AnyVal

case class ApplicationSectionId(id: Long) extends AnyVal

case class ApplicationSectionRow(id: ApplicationSectionId, applicationId: ApplicationId, sectionNumber: Int, title: String, started: Boolean)

case class ApplicationRow(id: ApplicationId, opportunityId: OpportunityId)

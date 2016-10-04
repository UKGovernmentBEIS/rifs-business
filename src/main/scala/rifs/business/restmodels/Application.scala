package rifs.business.restmodels

import rifs.models.{ApplicationId, OpportunityId}

case class ApplicationSection(sectionNumber: Int, title: String)

case class Application(id: ApplicationId, opportunityId: OpportunityId, sections: Seq[ApplicationSection])

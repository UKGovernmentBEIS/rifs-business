package rifs.business.restmodels

import rifs.business.models.{ApplicationId, OpportunityId}

case class ApplicationSection(sectionNumber: Int, title: String, started: Boolean)

case class Application(id: ApplicationId, opportunityId: OpportunityId, sections: Seq[ApplicationSection])

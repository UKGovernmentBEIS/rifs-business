package rifs.business.restmodels

import rifs.models.{ApplicationId, OpportunityId}

case class ApplicationSection(sectionNumber: Int, title: String, started: Boolean)

case class Application(id: ApplicationId, opportunityId: OpportunityId, sections: Seq[ApplicationSection])

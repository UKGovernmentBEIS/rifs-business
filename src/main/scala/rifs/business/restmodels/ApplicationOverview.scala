package rifs.business.restmodels

import org.joda.time.DateTime
import rifs.business.models.{ApplicationFormId, ApplicationId}

case class ApplicationSectionOverview(sectionNumber: Int, status: String, completedAt: Option[DateTime])

case class ApplicationOverview(id: ApplicationId, applicationFormId: ApplicationFormId, sections: Seq[ApplicationSectionOverview])
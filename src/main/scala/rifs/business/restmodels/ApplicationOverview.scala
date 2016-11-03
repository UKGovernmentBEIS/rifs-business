package rifs.business.restmodels

import org.joda.time.LocalDateTime
import rifs.business.models.{ApplicationFormId, ApplicationId}

//case class ApplicationSectionOverview(sectionNumber: Int, status: String, completedAt: Option[LocalDateTime])

case class ApplicationSectionOverview(sectionNumber: Int, completedAt: Option[LocalDateTime])

case class ApplicationOverview(id: ApplicationId, applicationFormId: ApplicationFormId, sections: Seq[ApplicationSectionOverview])
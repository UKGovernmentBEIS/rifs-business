package rifs.business.restmodels

import org.joda.time.LocalDateTime
import play.api.libs.json.JsObject
import rifs.business.models.{ApplicationFormId, ApplicationId}

case class ApplicationSection(sectionNumber: Int, answers: JsObject, completedAt: Option[LocalDateTime])

case class Application(id: ApplicationId, applicationFormId: ApplicationFormId, sections: Seq[ApplicationSection])

case class ApplicationDetail(
                              id: ApplicationId,
                              sectionCount: Int,
                              completedSectionCount: Int,
                              opportunity: OpportunitySummary,
                              applicationForm: ApplicationForm,
                              sections: Seq[ApplicationSection])

case class ApplicationSectionDetail(
                                     id: ApplicationId,
                                     sectionCount: Int,
                                     completedSectionCount: Int,
                                     opportunity: OpportunitySummary,
                                     formSection: ApplicationFormSection,
                                     section: Option[ApplicationSection]
                                   )

package rifs.business.restmodels

import org.joda.time.DateTime
import play.api.libs.json.JsObject
import rifs.business.models.{ApplicationFormId, ApplicationId}

case class ApplicationSection(sectionNumber: Int, answers: JsObject, completedAt: Option[DateTime])

case class Application(id: ApplicationId, applicationFormId: ApplicationFormId, personalReference: Option[String], sections: Seq[ApplicationSection])

case class ApplicationDetail(
                              id: ApplicationId,
                              personalReference: Option[String],
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

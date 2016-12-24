/*
 * Copyright (C) 2016  Department for Business, Energy and Industrial Strategy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

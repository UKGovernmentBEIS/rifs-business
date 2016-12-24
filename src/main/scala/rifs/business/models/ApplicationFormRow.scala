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

package rifs.business.models

import play.api.libs.json.JsArray

case class ApplicationFormId(id: Long) extends AnyVal

case class ApplicationFormSectionId(id: Long) extends AnyVal

case class ApplicationFormQuestionId(id: Long) extends AnyVal

case class ApplicationFormSectionRow(
                                      id: ApplicationFormSectionId,
                                      applicationFormId: ApplicationFormId,
                                      sectionNumber: Int,
                                      title: String,
                                      sectionType: String,
                                      fields: JsArray
                                    )

case class ApplicationFormRow(id: ApplicationFormId, opportunityId: OpportunityId)

case class ApplicationFormQuestionRow(
                                       id: ApplicationFormQuestionId,
                                       applicationFormSectionId: ApplicationFormSectionId,
                                       key: String,
                                       text: String,
                                       description: Option[String],
                                       helpText: Option[String])
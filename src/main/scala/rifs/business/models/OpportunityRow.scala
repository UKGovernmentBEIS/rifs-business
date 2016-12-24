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

import org.joda.time.DateTime

case class SectionId(id: Long) extends AnyVal

case class SectionRow(
                       id: SectionId,
                       sectionNumber: Int,
                       opportunityId: OpportunityId,
                       title: String,
                       text: Option[String],
                       description: Option[String],
                       helpText: Option[String],
                       sectionType: String
                     )

case class OpportunityId(id: Long) extends AnyVal

case class OpportunityRow(
                           id: OpportunityId,
                           title: String,
                           startDate: String,
                           endDate: Option[String],
                           value: BigDecimal,
                           valueUnits: String,
                           publishedAt: Option[DateTime],
                           duplicatedFrom: Option[OpportunityId]
                         )
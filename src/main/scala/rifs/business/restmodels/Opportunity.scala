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
import rifs.business.models.OpportunityId

case class OpportunityDescriptionSection(
                                          sectionNumber: Int,
                                          title: String,
                                          text: Option[String],
                                          description: String,
                                          helpText: Option[String],
                                          sectionType: String
                                        )

case class OpportunityValue(amount: BigDecimal, unit: String)

case class OpportunityDuration(duration: Int, units: String)

case class Opportunity(
                        id: OpportunityId,
                        title: String,
                        startDate: String,
                        endDate: Option[String],
                        value: OpportunityValue,
                        publishedAt: Option[DateTime],
                        duplicatedFrom: Option[OpportunityId],
                        description: Set[OpportunityDescriptionSection]
                      ) {
  def summary: OpportunitySummary = OpportunitySummary(id, title, startDate, endDate, value, publishedAt, duplicatedFrom)
}

case class OpportunitySummary(
                               id: OpportunityId,
                               title: String,
                               startDate: String,
                               endDate: Option[String],
                               value: OpportunityValue,
                               publishedAt: Option[DateTime],
                               duplicatedFrom: Option[OpportunityId]
                             )

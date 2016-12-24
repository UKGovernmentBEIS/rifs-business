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

package rifs.business.tables

import rifs.business.models.{OpportunityRow, SectionRow}
import rifs.business.restmodels.{Opportunity, OpportunityDescriptionSection, OpportunityValue}

object OpportunityExtractors {
  /**
    * Accepts the results of the left join across the Opportunity and Section tables and
    * builds them up into the REST model.
    *
    * @param joinResults the raw results from the left joins.
    */
  def extractOpportunities(joinResults: Seq[(OpportunityRow, Option[SectionRow])]): Set[Opportunity] = {
    val (oppRows, sectionRows) = unzipO(joinResults)

    oppRows.map { o =>
      Opportunity(
        o.id,
        o.title,
        o.startDate,
        o.endDate,
        OpportunityValue(o.value, o.valueUnits),
        o.publishedAt,
        o.duplicatedFrom,
        sectionsFor(o, sectionRows.toSet))
    }.toSet
  }

  def sectionsFor(o: OpportunityRow, ss: Set[SectionRow]): Set[OpportunityDescriptionSection] = {
    ss.filter(_.opportunityId == o.id).map(s => buildSection(s))
  }

  def buildSection(s: SectionRow): OpportunityDescriptionSection = {
    OpportunityDescriptionSection(s.sectionNumber, s.title, s.text, s.description, s.helpText, s.sectionType)
  }


  /**
    * useful for extracting from leftJoin results where the second item in the pair is represented as
    * an Option
    */
  def unzipO[A, B](join: Seq[(A, Option[B])]): (Seq[A], Seq[B]) = join.unzip match {
    case (as, bs) => (as, bs.flatten)
  }
}

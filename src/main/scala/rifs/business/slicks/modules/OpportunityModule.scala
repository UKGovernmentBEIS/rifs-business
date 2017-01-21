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

package rifs.business.slicks.modules

import com.wellfactored.slickgen.IdType
import org.joda.time.DateTime
import rifs.business.models._
import rifs.business.slicks.support.DBBinding

trait OpportunityModule extends DBBinding {
  import api._

  implicit def SectionIdMapper: BaseColumnType[SectionId] = MappedColumnType.base[SectionId, Long](_.id, SectionId)

  implicit def OpportunityIdMapper: BaseColumnType[OpportunityId] = MappedColumnType.base[OpportunityId, Long](_.id, OpportunityId)

  type SectionQuery = Query[SectionTable, SectionRow, Seq]

  class SectionTable(tag: Tag) extends Table[SectionRow](tag, "section") {
    def id = column[SectionId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def sectionNumber = column[Int]("section_number")

    def opportunityId = column[OpportunityId]("opportunity_id", O.Length(IdType.length))

    def opportunityIdFK = foreignKey("section_opportunity_fk", opportunityId, opportunityTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def opportunityIdIndex = index("section_opportunity_idx", opportunityId)

    def title = column[String]("title", O.Length(100))

    def text = column[Option[String]]("text", O.Length(8192))

    def description = column[String]("description", O.Length(8192))

    def helpText = column[Option[String]]("help_text", O.Length(8192))

    def sectionType = column[String]("section_type", O.Length(50))

    def * = (id, sectionNumber, opportunityId, title, text, description, helpText, sectionType) <> (SectionRow.tupled, SectionRow.unapply)
  }

  lazy val sectionTable = TableQuery[SectionTable]

  type OpportunityQuery = Query[OpportunityTable, OpportunityRow, Seq]

  class OpportunityTable(tag: Tag) extends Table[OpportunityRow](tag, "opportunity") {
    def id = column[OpportunityId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def title = column[String]("title", O.Length(100))

    def startAt = column[DateTime]("start_at_dtime")

    def endAt = column[Option[DateTime]]("end_at_dtime", O.Length(255))

    def value = column[BigDecimal]("value", O.SqlType("decimal(9, 2)"))

    def valueUnits = column[String]("value_units", O.Length(100))

    def publishedAt = column[Option[DateTime]]("published_at_dtime")

    def duplicatedFrom = column[Option[OpportunityId]]("duplicated_from_id")

    def duplicatedFromIdFK = foreignKey("duplicated_opportunity_fk", duplicatedFrom, opportunityTable)(_.id.?, onDelete = ForeignKeyAction.Cascade)

    def opportunityIdIndex = index("duplicated_opportunity_idx", duplicatedFrom)


    def * = (id, title, startAt, endAt, value, valueUnits, publishedAt, duplicatedFrom) <> (OpportunityRow.tupled, OpportunityRow.unapply)
  }

  lazy val opportunityTable = TableQuery[OpportunityTable]

  override def schema = super.schema ++ sectionTable.schema ++ opportunityTable.schema
}

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

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import com.wellfactored.slickgen.IdType
import play.api.libs.json.JsArray
import rifs.business.models._
import rifs.business.slicks.support.DBBinding

trait ApplicationFormModule extends PlayJsonMappers{
  self:  DBBinding with ExPostgresDriver with PgDateSupportJoda with PgPlayJsonSupport with OpportunityModule =>

  import api._

  implicit def ApplicationFormQuestionIdMapper: BaseColumnType[ApplicationFormQuestionId] = MappedColumnType.base[ApplicationFormQuestionId, Long](_.id, ApplicationFormQuestionId)

  implicit def ApplicationFormSectionIdMapper: BaseColumnType[ApplicationFormSectionId] = MappedColumnType.base[ApplicationFormSectionId, Long](_.id, ApplicationFormSectionId)

  implicit def ApplicationFormIdMapper: BaseColumnType[ApplicationFormId] = MappedColumnType.base[ApplicationFormId, Long](_.id, ApplicationFormId)

  type ApplicationFormQuestionQuery = Query[ApplicationFormQuestionTable, ApplicationFormQuestionRow, Seq]

  class ApplicationFormQuestionTable(tag: Tag) extends Table[ApplicationFormQuestionRow](tag, "application_form_question") {
    def id = column[ApplicationFormQuestionId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def applicationFormSectionId = column[ApplicationFormSectionId]("application_form_section_id", O.Length(IdType.length))

    def applicationFormSectionIdFK = foreignKey("applicationformquestion_application_form_section_fk", applicationFormSectionId, applicationFormSectionTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def applicationFormSectionIdIndex = index("applicationformquestion_application_form_section_idx", applicationFormSectionId)

    def key = column[String]("key", O.Length(255))

    def text = column[String]("text", O.Length(255))

    def description = column[Option[String]]("description", O.Length(255))

    def helpText = column[Option[String]]("help_text", O.Length(255))

    def * = (id, applicationFormSectionId, key, text, description, helpText) <> (ApplicationFormQuestionRow.tupled, ApplicationFormQuestionRow.unapply)
  }

  lazy val applicationFormQuestionTable = TableQuery[ApplicationFormQuestionTable]

  type ApplicationFormSectionQuery = Query[ApplicationFormSectionTable, ApplicationFormSectionRow, Seq]

  class ApplicationFormSectionTable(tag: Tag) extends Table[ApplicationFormSectionRow](tag, "application_form_section") {
    def id = column[ApplicationFormSectionId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def applicationFormId = column[ApplicationFormId]("application_form_id", O.Length(IdType.length))

    def applicationFormIdFK = foreignKey("applicationformsection_applicationform_fk", applicationFormId, applicationFormTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def applicationFormIdIndex = index("applicationformsection_applicationform_idx", applicationFormId)

    def sectionNumber = column[Int]("section_number")

    def title = column[String]("title", O.Length(255))

    def sectionType = column[String]("section_type", O.Length(50))

    def fields = column[JsArray]("fields")

    def * = (id, applicationFormId, sectionNumber, title, sectionType, fields) <> (ApplicationFormSectionRow.tupled, ApplicationFormSectionRow.unapply)
  }

  lazy val applicationFormSectionTable = TableQuery[ApplicationFormSectionTable]

  type ApplicationFormQuery = Query[ApplicationFormTable, ApplicationFormRow, Seq]

  class ApplicationFormTable(tag: Tag) extends Table[ApplicationFormRow](tag, "application_form") {
    def id = column[ApplicationFormId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def opportunityId = column[OpportunityId]("opportunity_id", O.Length(IdType.length))

    def opportunityIdFK = foreignKey("applicationform_opportunity_fk", opportunityId, opportunityTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def opportunityIdIndex = index("applicationform_opportunity_idx", opportunityId)

    def * = (id, opportunityId) <> (ApplicationFormRow.tupled, ApplicationFormRow.unapply)
  }

  lazy val applicationFormTable = TableQuery[ApplicationFormTable]

  //def schema: driver.DDL = applicationFormQuestionTable.schema ++ applicationFormSectionTable.schema ++ applicationFormTable.schema
}
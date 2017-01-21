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
import org.joda.time.DateTime
import play.api.libs.json.JsObject
import rifs.business.models._
import rifs.business.slicks.support.DBBinding

import scala.language.implicitConversions

trait ApplicationModule extends DBBinding with PlayJsonMappers {
  self: ExPostgresDriver with PgDateSupportJoda with PgPlayJsonSupport with ApplicationFormModule =>

  import api._


  implicit def ApplicationSectionIdMapper: BaseColumnType[ApplicationSectionId] = MappedColumnType.base[ApplicationSectionId, Long](_.id, ApplicationSectionId)

  implicit def ApplicationIdMapper: BaseColumnType[ApplicationId] = MappedColumnType.base[ApplicationId, Long](_.id, ApplicationId)

  type ApplicationSectionQuery = Query[ApplicationSectionTable, ApplicationSectionRow, Seq]

  class ApplicationSectionTable(tag: Tag) extends Table[ApplicationSectionRow](tag, "application_section") {
    def id = column[ApplicationSectionId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def applicationId = column[ApplicationId]("application_id", O.Length(IdType.length))

    def applicationIdFK = foreignKey("applicationsection_application_fk", applicationId, applicationTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def applicationIdIndex = index("applicationsection_application_idx", applicationId)

    def sectionNumber = column[Int]("section_number")

    def uniqueSectionNumberPerApplication = index("unique_section_number_per_application", (applicationId, sectionNumber), unique = true)

    def answers = column[JsObject]("answers")

    def completedAt = column[Option[DateTime]]("completed_at_dt")

    def * = (id, applicationId, sectionNumber, answers, completedAt) <> (ApplicationSectionRow.tupled, ApplicationSectionRow.unapply)
  }

  lazy val applicationSectionTable = TableQuery[ApplicationSectionTable]

  type ApplicationQuery = Query[ApplicationTable, ApplicationRow, Seq]

  class ApplicationTable(tag: Tag) extends Table[ApplicationRow](tag, "application") {
    def id = column[ApplicationId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def applicationFormId = column[ApplicationFormId]("application_form_id", O.Length(IdType.length))

    def personalReference = column[Option[String]]("personal_reference", O.Length(100))

    def applicationFormIdFK = foreignKey("application_application_form_fk", applicationFormId, applicationFormTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def applicationFormIdIndex = index("application_application_form_idx", applicationFormId)

    def * = (id, applicationFormId, personalReference) <> (ApplicationRow.tupled, ApplicationRow.unapply)
  }

  lazy val applicationTable = TableQuery[ApplicationTable]

  override def schema = super.schema ++ applicationSectionTable.schema ++ applicationTable.schema

}
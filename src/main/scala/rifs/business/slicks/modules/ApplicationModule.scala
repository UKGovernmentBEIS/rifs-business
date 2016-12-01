package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import org.joda.time.DateTime
import play.api.libs.json.JsObject
import rifs.business.models._
import rifs.business.slicks.support.DBBinding
import rifs.slicks.gen.IdType

import scala.language.implicitConversions

trait ApplicationModule extends PlayJsonMappers {
  self: DBBinding with ExPostgresDriver with PgDateSupportJoda with PgPlayJsonSupport with ApplicationFormModule =>

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

    def answers = column[JsObject]("answers")

    def completedAt = column[Option[DateTime]]("completed_at_dt")

    def * = (id.?, applicationId, sectionNumber, answers, completedAt) <> (ApplicationSectionRow.tupled, ApplicationSectionRow.unapply)
  }

  lazy val applicationSectionTable = TableQuery[ApplicationSectionTable]

  type ApplicationQuery = Query[ApplicationTable, ApplicationRow, Seq]

  class ApplicationTable(tag: Tag) extends Table[ApplicationRow](tag, "application") {
    def id = column[ApplicationId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def applicationFormId = column[ApplicationFormId]("application_form_id", O.Length(IdType.length))

    def applicationFormIdFK = foreignKey("application_application_form_fk", applicationFormId, applicationFormTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def applicationFormIdIndex = index("application_application_form_idx", applicationFormId)

    def * = (id.?, applicationFormId) <> (ApplicationRow.tupled, ApplicationRow.unapply)
  }

  lazy val applicationTable = TableQuery[ApplicationTable]

  def create = (applicationSectionTable.schema ++ applicationTable.schema).createStatements

}
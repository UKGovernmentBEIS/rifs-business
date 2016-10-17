package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgPlayJsonSupport}
import play.api.libs.json.JsValue
import rifs.business.models._
import rifs.business.slicks.support.DBBinding
import rifs.slicks.gen.IdType

trait ApplicationModule {
  self: ExPostgresDriver with PgPlayJsonSupport with DBBinding with ApplicationFormModule =>

  object PostgresAPI extends API with JsonImplicits

  override val pgjson = "jsonb"

  import PostgresAPI._

  implicit def ApplicationSectionIdMapper: BaseColumnType[ApplicationSectionId] = MappedColumnType.base[ApplicationSectionId, Long](_.id, ApplicationSectionId)

  implicit def ApplicationIdMapper: BaseColumnType[ApplicationId] = MappedColumnType.base[ApplicationId, Long](_.id, ApplicationId)

  type ApplicationSectionQuery = Query[ApplicationSectionTable, ApplicationSectionRow, Seq]

  class ApplicationSectionTable(tag: Tag) extends Table[ApplicationSectionRow](tag, "application_section") {
    def id = column[ApplicationSectionId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def applicationId = column[ApplicationId]("application_id", O.Length(IdType.length))

    def applicationIdFK = foreignKey("applicationsection_application_fk", applicationId, applicationTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def applicationIdIndex = index("applicationsection_application_idx", applicationId)

    def sectionNumber = column[Int]("section_number")

    def answers = column[JsValue]("answers")

    def * = (id.?, applicationId, sectionNumber, answers) <> (ApplicationSectionRow.tupled, ApplicationSectionRow.unapply)
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
package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import play.api.libs.json.JsArray
import rifs.business.models._
import rifs.business.slicks.support.DBBinding
import rifs.slicks.gen.IdType

trait ApplicationFormModule {
  self: ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda with DBBinding with PlayJsonMappers with OpportunityModule =>

  object pgApi extends API with JsonImplicits with JodaDateTimeImplicits

  override val pgjson = "jsonb"

  import pgApi._

  implicit def ApplicationFormQuestionIdMapper: BaseColumnType[ApplicationFormQuestionId] = MappedColumnType.base[ApplicationFormQuestionId, Long](_.id, ApplicationFormQuestionId)

  implicit def ApplicationFormSectionIdMapper: BaseColumnType[ApplicationFormSectionId] = MappedColumnType.base[ApplicationFormSectionId, Long](_.id, ApplicationFormSectionId)

  implicit def ApplicationFormIdMapper: BaseColumnType[ApplicationFormId] = MappedColumnType.base[ApplicationFormId, Long](_.id, ApplicationFormId)

  type ApplicationFormQuestionQuery = Query[ApplicationFormQuestionTable, ApplicationFormQuestionRow, Seq]

  class ApplicationFormQuestionTable(tag: Tag) extends Table[ApplicationFormQuestionRow](tag, "application_form_question") {
    def id = column[ApplicationFormQuestionId]("id", O.Length(IdType.length), O.PrimaryKey)

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
    def id = column[ApplicationFormSectionId]("id", O.Length(IdType.length), O.PrimaryKey)

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
    def id = column[ApplicationFormId]("id", O.Length(IdType.length), O.PrimaryKey)

    def opportunityId = column[OpportunityId]("opportunity_id", O.Length(IdType.length))

    def opportunityIdFK = foreignKey("applicationform_opportunity_fk", opportunityId, opportunityTable)(_.id, onDelete = ForeignKeyAction.Cascade)

    def opportunityIdIndex = index("applicationform_opportunity_idx", opportunityId)

    def * = (id, opportunityId) <> (ApplicationFormRow.tupled, ApplicationFormRow.unapply)
  }

  lazy val applicationFormTable = TableQuery[ApplicationFormTable]

  //def schema: driver.DDL = applicationFormQuestionTable.schema ++ applicationFormSectionTable.schema ++ applicationFormTable.schema
}
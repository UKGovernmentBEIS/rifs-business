package rifs.business.slicks.modules

import rifs.business.models._
import rifs.slicks.gen.IdType
import rifs.business.slicks.support.DBBinding

trait ApplicationFormModule {
  self: DBBinding with OpportunityModule =>
  import driver.api._
  implicit def ApplicationSectionIdMapper: BaseColumnType[ApplicationFormSectionId] = MappedColumnType.base[ApplicationFormSectionId, Long](_.id, ApplicationFormSectionId)
  implicit def ApplicationIdMapper: BaseColumnType[ApplicationFormId] = MappedColumnType.base[ApplicationFormId, Long](_.id, ApplicationFormId)

  type ApplicationSectionQuery = Query[ApplicationFormSectionTable, ApplicationFormSectionRow, Seq]
  class ApplicationFormSectionTable(tag: Tag) extends Table[ApplicationFormSectionRow](tag, "application_form_section") {
    def id = column[ApplicationFormSectionId]("id", O.Length(IdType.length), O.PrimaryKey)
    def applicationFormId = column[ApplicationFormId]("application_form_id", O.Length(IdType.length))
    def applicationFormIdFK = foreignKey("applicationformsection_applicationform_fk", applicationFormId, applicationFormTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def applicationFormIdIndex = index("applicationformsection_applicationform_idx", applicationFormId)
    def sectionNumber = column[Int]("section_number")
    def title = column[String]("title", O.Length(255))
    def started = column[Boolean]("started")
    def * = (id, applicationFormId, sectionNumber, title, started) <> (ApplicationFormSectionRow.tupled, ApplicationFormSectionRow.unapply)
  }
  lazy val applicationFormSectionTable = TableQuery[ApplicationFormSectionTable]

  type ApplicationQuery = Query[ApplicationFormTable, ApplicationFormRow, Seq]
  class ApplicationFormTable(tag: Tag) extends Table[ApplicationFormRow](tag, "application_form") {
    def id = column[ApplicationFormId]("id", O.Length(IdType.length), O.PrimaryKey)
    def opportunityId = column[OpportunityId]("opportunity_id", O.Length(IdType.length))
    def opportunityIdFK = foreignKey("applicationform_opportunity_fk", opportunityId, opportunityTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def opportunityIdIndex = index("applicationform_opportunity_idx", opportunityId)
    def * = (id, opportunityId) <> (ApplicationFormRow.tupled, ApplicationFormRow.unapply)
  }
  lazy val applicationFormTable = TableQuery[ApplicationFormTable]

  def createApplication = (applicationFormSectionTable.schema ++ applicationFormTable.schema).createStatements
}
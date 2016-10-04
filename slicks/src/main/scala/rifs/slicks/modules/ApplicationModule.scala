package rifs.slicks.modules

import rifs.models._
import rifs.slicks.gen.IdType
import rifs.slicks.support.DBBinding

trait ApplicationModule {
  self: DBBinding with OpportunityModule =>
  import driver.api._
  implicit def ApplicationSectionIdMapper: BaseColumnType[ApplicationSectionId] = MappedColumnType.base[ApplicationSectionId, Long](_.id, ApplicationSectionId)
  implicit def ApplicationIdMapper: BaseColumnType[ApplicationId] = MappedColumnType.base[ApplicationId, Long](_.id, ApplicationId)

  type ApplicationSectionQuery = Query[ApplicationSectionTable, ApplicationSectionRow, Seq]
  class ApplicationSectionTable(tag: Tag) extends Table[ApplicationSectionRow](tag, "application_section") {
    def id = column[ApplicationSectionId]("id", O.Length(IdType.length), O.PrimaryKey)
    def applicationId = column[ApplicationId]("application_id", O.Length(IdType.length))
    def applicationIdFK = foreignKey("applicationsection_application_fk", applicationId, applicationTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def applicationIdIndex = index("applicationsection_application_idx", applicationId)
    def sectionNumber = column[Int]("section_number")
    def title = column[String]("title", O.Length(255))
    def started = column[Boolean]("started")
    def * = (id, applicationId, sectionNumber, title, started) <> (ApplicationSectionRow.tupled, ApplicationSectionRow.unapply)
  }
  lazy val applicationSectionTable = TableQuery[ApplicationSectionTable]

  type ApplicationQuery = Query[ApplicationTable, ApplicationRow, Seq]
  class ApplicationTable(tag: Tag) extends Table[ApplicationRow](tag, "application") {
    def id = column[ApplicationId]("id", O.Length(IdType.length), O.PrimaryKey)
    def opportunityId = column[OpportunityId]("opportunity_id", O.Length(IdType.length))
    def opportunityIdFK = foreignKey("application_opportunity_fk", opportunityId, opportunityTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def opportunityIdIndex = index("application_opportunity_idx", opportunityId)
    def * = (id, opportunityId) <> (ApplicationRow.tupled, ApplicationRow.unapply)
  }
  lazy val applicationTable = TableQuery[ApplicationTable]

   def createApplication = (applicationSectionTable.schema ++ applicationTable.schema).createStatements
}
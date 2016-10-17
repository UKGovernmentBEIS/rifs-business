package rifs.business.slicks.modules

import rifs.business.models._
import rifs.slicks.gen.IdType
import rifs.business.slicks.support.DBBinding

trait OpportunityModule {
  self: DBBinding =>
  import driver.api._
  implicit def ParagraphIdMapper: BaseColumnType[ParagraphId] = MappedColumnType.base[ParagraphId, Long](_.id, ParagraphId)
  implicit def SectionIdMapper: BaseColumnType[SectionId] = MappedColumnType.base[SectionId, Long](_.id, SectionId)
  implicit def OpportunityIdMapper: BaseColumnType[OpportunityId] = MappedColumnType.base[OpportunityId, Long](_.id, OpportunityId)

  type ParagraphQuery = Query[ParagraphTable, ParagraphRow, Seq]
  class ParagraphTable(tag: Tag) extends Table[ParagraphRow](tag, "paragraph") {
    def id = column[ParagraphId]("id", O.Length(IdType.length), O.PrimaryKey)
    def paragraphNumber = column[Int]("paragraph_number")
    def sectionId = column[SectionId]("section_id", O.Length(IdType.length))
    def sectionIdFK = foreignKey("paragraph_section_fk", sectionId, sectionTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def sectionIdIndex = index("paragraph_section_idx", sectionId)
    def text = column[String]("text", O.Length(255))
    def * = (id, paragraphNumber, sectionId, text) <> (ParagraphRow.tupled, ParagraphRow.unapply)
  }
  lazy val paragraphTable = TableQuery[ParagraphTable]

  type SectionQuery = Query[SectionTable, SectionRow, Seq]
  class SectionTable(tag: Tag) extends Table[SectionRow](tag, "section") {
    def id = column[SectionId]("id", O.Length(IdType.length), O.PrimaryKey)
    def sectionNumber = column[Int]("section_number")
    def opportunityId = column[OpportunityId]("opportunity_id", O.Length(IdType.length))
    def opportunityIdFK = foreignKey("section_opportunity_fk", opportunityId, opportunityTable)(_.id, onDelete = ForeignKeyAction.Cascade)
    def opportunityIdIndex = index("section_opportunity_idx", opportunityId)
    def title = column[String]("title", O.Length(255))
    def * = (id, sectionNumber, opportunityId, title) <> (SectionRow.tupled, SectionRow.unapply)
  }
  lazy val sectionTable = TableQuery[SectionTable]

  type OpportunityQuery = Query[OpportunityTable, OpportunityRow, Seq]
  class OpportunityTable(tag: Tag) extends Table[OpportunityRow](tag, "opportunity") {
    def id = column[OpportunityId]("id", O.Length(IdType.length), O.PrimaryKey)
    def title = column[String]("title", O.Length(255))
    def startDate = column[String]("start_date", O.Length(255))
    def duration = column[Option[Int]]("duration")
    def durationUnits = column[Option[String]]("duration_units", O.Length(255))
    def value = column[BigDecimal]("value", O.SqlType("decimal(9, 2)"))
    def valueUnits = column[String]("value_units", O.Length(255))
    def * = (id, title, startDate, duration, durationUnits, value, valueUnits) <> (OpportunityRow.tupled, OpportunityRow.unapply)
  }
  lazy val opportunityTable = TableQuery[OpportunityTable]

  def create = (paragraphTable.schema ++ sectionTable.schema ++ opportunityTable.schema).createStatements
}

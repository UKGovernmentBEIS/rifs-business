package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda}
import org.joda.time.DateTime
import rifs.business.models._
import rifs.business.slicks.support.DBBinding
import rifs.slicks.gen.IdType

trait OpportunityModule {
  self: DBBinding =>

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

    def title = column[String]("title", O.Length(255))

    def text = column[Option[String]]("text", O.Length(8192))

    def * = (id, sectionNumber, opportunityId, title, text) <> (SectionRow.tupled, SectionRow.unapply)
  }

  lazy val sectionTable = TableQuery[SectionTable]

  type OpportunityQuery = Query[OpportunityTable, OpportunityRow, Seq]

  class OpportunityTable(tag: Tag) extends Table[OpportunityRow](tag, "opportunity") {
    def id = column[OpportunityId]("id", O.Length(IdType.length), O.PrimaryKey, O.AutoInc)

    def title = column[String]("title", O.Length(255))

    def startDate = column[String]("start_date", O.Length(255))

    def endDate = column[Option[String]]("end_date", O.Length(255))

    def value = column[BigDecimal]("value", O.SqlType("decimal(9, 2)"))

    def valueUnits = column[String]("value_units", O.Length(255))

    def publishedAt = column[Option[DateTime]]("published_at_dtime")

    def duplicatedFrom = column[Option[OpportunityId]]("duplicated_from_id")

    def duplicatedFromIdFK = foreignKey("duplicated_opportunity_fk", duplicatedFrom, opportunityTable)(_.id.?, onDelete = ForeignKeyAction.Cascade)

    def opportunityIdIndex = index("duplicated_opportunity_idx", duplicatedFrom)


    def * = (id, title, startDate, endDate, value, valueUnits, publishedAt, duplicatedFrom) <> (OpportunityRow.tupled, OpportunityRow.unapply)
  }

  lazy val opportunityTable = TableQuery[OpportunityTable]
}

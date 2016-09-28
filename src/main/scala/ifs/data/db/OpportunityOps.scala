package ifs.data.db

import javax.inject.Inject

import com.google.inject.ImplementedBy
import ifs.data.restmodels.{Opportunity, OpportunityDescriptionSection, OpportunityDuration, OpportunityValue}
import ifs.models._
import ifs.slicks.modules.OpportunityModule
import ifs.slicks.support.DBBinding
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OpportunityTables])
trait OpportunityOps {
  def byId(id: OpportunityId): Future[Option[OpportunityRow]]

  def sectionsForOpportunity(id: OpportunityId): Future[Seq[SectionRow]]

  def paragraphsForSections(sectionId: SectionId): Future[Seq[ParagraphRow]]

  def byIdWithDescription(opportunityId: OpportunityId): Future[Option[Opportunity]]

  def open: Future[Seq[OpportunityRow]]

}

class OpportunityTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends OpportunityModule with DBBinding with OpportunityOps {
  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  create.foreach(s => println(s"$s;"))

  import driver.api._

  override def byId(opportunityId: OpportunityId): Future[Option[OpportunityRow]] = db.run {
    opportunityTable.filter(_.id === opportunityId).result.headOption
  }

  def buildSections(rows: Seq[(SectionRow, Option[ParagraphRow])]): Seq[(OpportunityId, OpportunityDescriptionSection)] = {
    val grouped = rows.groupBy(_._1).map { case (s, ops) => s -> ops.flatMap(_._2) }
    grouped.map {
      case (section, paras) => section.opportunityId -> OpportunityDescriptionSection(section.sectionNumber, section.title, paras.map(_.text))
    }.toSeq
  }

  override def byIdWithDescription(id: OpportunityId): Future[Option[Opportunity]] = db.run {
    val q1 = for {
      o <- opportunityTable if o.id === id
    } yield o

    // TODO: Finish this off to extract the sections and paragraphs into the Opportunity
    val spQ = for {
      sp <- sectionTable joinLeft paragraphTable on (_.id === _.sectionId)
    } yield sp

    val q2 = for {
      os <- opportunityTable joinLeft spQ on (_.id === _._1.opportunityId) if os._1.id === id
    } yield os

    q1.result.headOption.map {
      _.map { o =>
        val od = for {
          d <- o.duration
          du <- o.durationUnits
        } yield OpportunityDuration(d, du)

        Opportunity(o.id, o.title, o.startDate, od, OpportunityValue(o.value, o.valueUnits), Seq())
      }
    }
  }

  override def open: Future[Seq[OpportunityRow]] = db.run(opportunityTable.result)

  override def sectionsForOpportunity(id: OpportunityId): Future[Seq[SectionRow]] = db.run(sectionTable.filter(_.opportunityId === id).result)

  override def paragraphsForSections(sectionId: SectionId): Future[Seq[ParagraphRow]] = db.run(paragraphTable.filter(_.sectionId === sectionId).result)
}
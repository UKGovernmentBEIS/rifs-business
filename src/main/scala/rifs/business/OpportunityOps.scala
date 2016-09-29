package rifs.business

import javax.inject.Inject

import com.google.inject.ImplementedBy
import rifs.models._
import rifs.slicks.modules.OpportunityModule
import play.api.db.slick.DatabaseConfigProvider
import rifs.business.restmodels._
import rifs.slicks.modules.OpportunityModule
import rifs.slicks.support.DBBinding
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

  def buildSections(rows: Seq[(SectionRow, Option[ParagraphRow])]): Map[OpportunityId, Seq[OpportunityDescriptionSection]] = {
    val grouped = rows.groupBy(_._1).map { case (s, ops) => s -> ops.flatMap(_._2) }
    grouped.map {
      case (section, paras) =>
        val paragraphTexts: Seq[String] = paras.sortBy(_.paragraphNumber).map(_.text)
        section.opportunityId -> OpportunityDescriptionSection(section.sectionNumber, section.title, paragraphTexts)
    }.toSeq.groupBy(_._1).map { case (oid, ss) => oid -> ss.map(_._2) }
  }

  override def byIdWithDescription(id: OpportunityId): Future[Option[Opportunity]] = db.run {
    val spQ = for {
      sp <- sectionTable joinLeft paragraphTable on (_.id === _.sectionId)
    } yield sp

    val q2 = for {
      os <- opportunityTable joinLeft spQ on (_.id === _._1.opportunityId) if os._1.id === id
    } yield os

    q2.result.map { x =>
      val sectionMap: Map[OpportunityId, Seq[OpportunityDescriptionSection]] = buildSections(x.flatMap(_._2))

      x.map { case (o, _) =>
        val sections = sectionMap.getOrElse(o.id, Seq())
        val od = for {
          d <- o.duration
          du <- o.durationUnits
        } yield OpportunityDuration(d, du)
        Opportunity(o.id, o.title, o.startDate, od, OpportunityValue(o.value, o.valueUnits), sections)
      }.headOption
    }


  }

  override def open: Future[Seq[OpportunityRow]] = db.run(opportunityTable.result)

  override def sectionsForOpportunity(id: OpportunityId): Future[Seq[SectionRow]] = db.run(sectionTable.filter(_.opportunityId === id).result)

  override def paragraphsForSections(sectionId: SectionId): Future[Seq[ParagraphRow]] = db.run(paragraphTable.filter(_.sectionId === sectionId).result)
}
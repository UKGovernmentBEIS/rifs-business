package rifs.business.tables

import javax.inject.Inject

import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.OpportunityOps
import rifs.business.models.{OpportunityId, OpportunityRow}
import rifs.business.restmodels.{Opportunity, OpportunitySummary, OpportunityValue}
import rifs.business.slicks.modules.{OpportunityModule, PgSupport}
import rifs.business.slicks.support.DBBinding

import scala.concurrent.{ExecutionContext, Future}

class OpportunityTables @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends OpportunityModule with DBBinding with OpportunityOps with PgSupport {

  import OpportunityExtractors._
  import api._

  override def byId(opportunityId: OpportunityId): Future[Option[OpportunityRow]] = db.run(byIdC(opportunityId).result.headOption)

  override def opportunity(id: OpportunityId): Future[Option[Opportunity]] = db.run(oppWithDescC(id).result.map(extractOpportunities(_).headOption))

  override def findOpen: Future[Set[Opportunity]] = db.run {
    joinedOppsWithSectionsC.result.map(extractOpportunities)
  }

  override def openSummaries: Future[Set[Opportunity]] = db.run(opportunityTableC.result).map { os =>
    os.map(o => Opportunity(o.id, o.title, o.startDate, o.endDate, OpportunityValue(o.value, o.valueUnits), o.publishedAt, o.duplicatedFrom, Set())).toSet
  }


  override def updateSummary(summary: OpportunitySummary): Future[Int] = db.run {
    val row = OpportunityRow(summary.id, summary.title, summary.startDate, summary.endDate, summary.value.amount, summary.value.unit, summary.publishedAt, summary.duplicatedFrom)
    byIdC(summary.id).update(row)
  }

  override def publish(id: OpportunityId): Future[Option[DateTime]] = ???

  override def duplicate(id: OpportunityId): Future[Option[OpportunityId]] = ???

  /** ****************************
    * Queries and compiled queries
    */
  def byIdQ(id: Rep[OpportunityId]) = opportunityTable.filter(_.id === id)

  val byIdC = Compiled(byIdQ _)

  val joinedOppsWithSections = for {
    os <- opportunityTable joinLeft sectionTable on (_.id === _.opportunityId)
  } yield os

  val joinedOppsWithSectionsC = Compiled(joinedOppsWithSections)

  def oppWithDescQ(id: Rep[OpportunityId]) = for {
    os <- joinedOppsWithSections if os._1.id === id
  } yield os

  val oppWithDescC = Compiled(oppWithDescQ _)

  val opportunityTableC = Compiled(opportunityTable.pack)


}

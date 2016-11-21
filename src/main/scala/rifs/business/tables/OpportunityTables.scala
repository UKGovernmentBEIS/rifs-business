package rifs.business.tables

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.OpportunityOps
import rifs.business.models.{OpportunityId, OpportunityRow}
import rifs.business.restmodels.{Opportunity, OpportunityValue}
import rifs.business.slicks.modules.OpportunityModule
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class OpportunityTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends OpportunityModule with DBBinding with OpportunityOps {

  import OpportunityExtractors._

  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import driver.api._

  override def byId(opportunityId: OpportunityId): Future[Option[OpportunityRow]] = db.run(byIdC(opportunityId).result.headOption)

  override def opportunity(id: OpportunityId): Future[Option[Opportunity]] = db.run(oppWithDescC(id).result.map(extractOpportunities(_).headOption))

  override def open: Future[Set[Opportunity]] = db.run {
    joinedOppsWithSectionsC.result.map(extractOpportunities)
  }

  override def openSummaries: Future[Set[Opportunity]] = db.run(opportunityTableC.result).map { os =>
    os.map(o => Opportunity(o.id, o.title, o.startDate, durationFor(o), OpportunityValue(o.value, o.valueUnits), Set())).toSet
  }

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

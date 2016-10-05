package rifs.business.tables

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.OpportunityOps
import rifs.business.restmodels.{Opportunity, OpportunityValue}
import rifs.models.{OpportunityId, OpportunityRow}
import rifs.slicks.modules.OpportunityModule
import rifs.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class OpportunityTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends OpportunityModule with DBBinding with OpportunityOps {

  import OpportunityExtractors._

  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import driver.api._

  override def byId(opportunityId: OpportunityId): Future[Option[OpportunityRow]] = db.run(byIdC(opportunityId).result.headOption)

  override def byIdWithDescription(id: OpportunityId): Future[Option[Opportunity]] = db.run(oppWithDescC(id).result.map(extractOpportunities(_).headOption))

  override def open: Future[Seq[Opportunity]] = db.run {
    joinedOppsWithSectionsC.result.map(extractOpportunities)
  }

  override def openSummaries: Future[Seq[Opportunity]] = db.run(opportunityTable.result).map { os =>
    os.map(o => Opportunity(o.id, o.title, o.startDate, durationFor(o), OpportunityValue(o.value, o.valueUnits), Seq()))
  }

  /*
 ******************************
 * Queries and compiled queries
  */
  def byIdQ(id: Rep[OpportunityId]) = opportunityTable.filter(_.id === id)

  val byIdC = Compiled(byIdQ _)

  val joinedSectionsWithParas = for {
    sp <- sectionTable joinLeft paragraphTable on (_.id === _.sectionId)
  } yield sp

  val joinedOppsWithSections = for {
    os <- opportunityTable joinLeft joinedSectionsWithParas on (_.id === _._1.opportunityId)
  } yield os

  val joinedOppsWithSectionsC = Compiled(joinedOppsWithSections)

  def oppWithDescQ(id: Rep[OpportunityId]) = for {
    os <- joinedOppsWithSections if os._1.id === id
  } yield os

  val oppWithDescC = Compiled(oppWithDescQ _)

}

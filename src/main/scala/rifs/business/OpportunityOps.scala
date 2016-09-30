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

  def byIdWithDescription(opportunityId: OpportunityId): Future[Option[Opportunity]]

  def open: Future[Seq[Opportunity]]
}

class OpportunityTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends OpportunityModule with DBBinding with OpportunityOps {

  import OpportunityExtractors._

  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  //create.foreach(s => println(s"$s;"))

  import driver.api._

  override def byId(opportunityId: OpportunityId): Future[Option[OpportunityRow]] = db.run {
    opportunityTable.filter(_.id === opportunityId).result.headOption
  }

  val joinedSectionsWithParas = for {
    sp <- sectionTable joinLeft paragraphTable on (_.id === _.sectionId)
  } yield sp

  override def byIdWithDescription(id: OpportunityId): Future[Option[Opportunity]] = db.run {
    val joinedOppWithDescription = for {
      os <- opportunityTable joinLeft joinedSectionsWithParas on (_.id === _._1.opportunityId) if os._1.id === id
    } yield os

    joinedOppWithDescription.result.map(extractOpportunities(_).headOption)
  }


  override def open: Future[Seq[Opportunity]] = db.run {
    val joinedOppWithDescription = for {
      os <- opportunityTable joinLeft joinedSectionsWithParas on (_.id === _._1.opportunityId)
    } yield os

    joinedOppWithDescription.result.map(extractOpportunities)
  }

}
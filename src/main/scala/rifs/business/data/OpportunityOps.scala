package rifs.business.data

import com.google.inject.ImplementedBy
import rifs.business.restmodels._
import rifs.business.tables.OpportunityTables
import rifs.models._

import scala.concurrent.Future

@ImplementedBy(classOf[OpportunityTables])
trait OpportunityOps {
  def byId(id: OpportunityId): Future[Option[OpportunityRow]]

  def byIdWithDescription(opportunityId: OpportunityId): Future[Option[Opportunity]]

  def open: Future[Set[Opportunity]]

  def openSummaries: Future[Set[Opportunity]]
}


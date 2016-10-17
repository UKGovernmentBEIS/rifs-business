package rifs.business.data

import com.google.inject.ImplementedBy
import rifs.business.models.{ApplicationId, OpportunityId}
import rifs.business.restmodels.Application
import rifs.business.tables.ApplicationTables

import scala.concurrent.Future

@ImplementedBy(classOf[ApplicationTables])
trait ApplicationOps {
  def byId(id: ApplicationId): Future[Option[Application]]

  def forOpportunity(opportunityId: OpportunityId): Future[Option[Application]]
}

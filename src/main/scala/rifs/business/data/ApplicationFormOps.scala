package rifs.business.data

import com.google.inject.ImplementedBy
import rifs.business.models.{ApplicationFormId, OpportunityId}
import rifs.business.restmodels.ApplicationForm
import rifs.business.tables.ApplicationFormFormTables

import scala.concurrent.Future

@ImplementedBy(classOf[ApplicationFormFormTables])
trait ApplicationFormOps {
  def byId(id: ApplicationFormId): Future[Option[ApplicationForm]]

  def forOpportunity(opportunityId: OpportunityId): Future[Option[ApplicationForm]]
}

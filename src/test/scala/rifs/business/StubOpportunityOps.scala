package rifs.business

import org.joda.time.DateTime
import rifs.business.data.OpportunityOps
import rifs.business.models.{OpportunityId, OpportunityRow}
import rifs.business.restmodels.{Opportunity, OpportunitySummary}

import scala.concurrent.Future

class StubOpportunityOps extends OpportunityOps {

  override def byId(id: OpportunityId): Future[Option[OpportunityRow]] = ???

  override def opportunity(opportunityId: OpportunityId): Future[Option[Opportunity]] = ???

  override def findOpen: Future[Set[Opportunity]] = ???

  override def summaries: Future[Set[Opportunity]] = ???

  override def openSummaries: Future[Set[Opportunity]] = ???

  override def updateSummary(summary: OpportunitySummary): Future[Int] = ???

  override def publish(id: OpportunityId): Future[Option[DateTime]] = ???

  override def duplicate(id: OpportunityId): Future[Option[OpportunityId]] = ???

  override def saveSectionDescription(id: OpportunityId, sectionNo: Int, description: Option[String]): Future[Int] = ???

  override def reset(): Future[Unit] = ???
}

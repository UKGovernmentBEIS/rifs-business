package rifs.business.data

import com.google.inject.ImplementedBy
import org.joda.time.DateTime
import rifs.business.models.{OpportunityId, OpportunityRow}
import rifs.business.restmodels._
import rifs.business.tables.OpportunityTables

import scala.concurrent.Future

@ImplementedBy(classOf[OpportunityTables])
trait OpportunityOps {
  def byId(id: OpportunityId): Future[Option[OpportunityRow]]

  def opportunity(opportunityId: OpportunityId): Future[Option[Opportunity]]

  def findOpen: Future[Set[Opportunity]]

  def summaries: Future[Set[Opportunity]]

  def openSummaries: Future[Set[Opportunity]]

  def updateSummary(summary: OpportunitySummary): Future[Int]

  def publish(id: OpportunityId): Future[Option[DateTime]]

  def duplicate(id: OpportunityId): Future[Option[OpportunityId]]

  def saveSectionDescription(id: OpportunityId, sectionNo: Int, description: Option[String]): Future[Int]

  def updateGrantValue(id: OpportunityId, value: BigDecimal) : Future[Int]

  def reset(): Future[Unit]
}


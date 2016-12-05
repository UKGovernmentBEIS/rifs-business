package rifs.business.tables

import javax.inject.Inject

import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.OpportunityOps
import rifs.business.models.{OpportunityId, OpportunityRow}
import rifs.business.restmodels.{Opportunity, OpportunitySummary, OpportunityValue}
import rifs.business.slicks.modules.{OpportunityModule, PgSupport}
import rifs.business.slicks.support.DBBinding
import slick.dbio.DBIOAction

import scala.concurrent.{ExecutionContext, Future}

class OpportunityTables @Inject()(val dbConfigProvider: DatabaseConfigProvider, appFormTables: ApplicationFormTables)(implicit ec: ExecutionContext)
  extends OpportunityModule
    with DBBinding
    with OpportunityOps
    with PgSupport {

  import OpportunityExtractors._
  import api._

  override def byId(opportunityId: OpportunityId): Future[Option[OpportunityRow]] = db.run(byIdC(opportunityId).result.headOption)

  override def opportunity(id: OpportunityId): Future[Option[Opportunity]] = db.run(oppWithDescC(id).result.map(extractOpportunities(_).headOption))

  override def findOpen: Future[Set[Opportunity]] = db.run {
    joinedOppsWithSectionsC.result.map(extractOpportunities)
  }

  override def summaries: Future[Set[Opportunity]] = db.run(opportunityTableC.result).map { os =>
    os.map(o => Opportunity(o.id, o.title, o.startDate, o.endDate, OpportunityValue(o.value, o.valueUnits), o.publishedAt, o.duplicatedFrom, Set())).toSet
  }

  override def openSummaries: Future[Set[Opportunity]] = db.run(opportunityTable.filter(_.publishedAt.isDefined).result).map { os =>
    os.map(o => Opportunity(o.id, o.title, o.startDate, o.endDate, OpportunityValue(o.value, o.valueUnits), o.publishedAt, o.duplicatedFrom, Set())).toSet
  }

  override def updateSummary(summary: OpportunitySummary): Future[Int] = db.run {
    val row = OpportunityRow(summary.id, summary.title, summary.startDate, summary.endDate, summary.value.amount, summary.value.unit, summary.publishedAt, summary.duplicatedFrom)
    byIdC(summary.id).update(row)
  }

  override def publish(id: OpportunityId): Future[Option[DateTime]] = db.run {
    byIdC(id).result.headOption.flatMap {
      case Some(opp) =>
        if (opp.publishedAt.isDefined) {
          DBIO.successful(opp.publishedAt)
        } else {
          val publishedAt = Some(DateTime.now())
          byIdC(id).update(opp.copy(publishedAt = publishedAt)).map { _ => publishedAt }
        }
      case None => DBIO.successful(None)
    }.transactionally
  }

  override def duplicate(id: OpportunityId): Future[Option[OpportunityId]] = {
    val action: DBIO[Option[OpportunityId]] = byIdC(id).result.flatMap {
      _.headOption match {
        case None => DBIOAction.successful(None)

        case Some(opp) =>
          for {
            newId <- duplicateOpportunity(id, opp)
            _ <- duplicateOpportunitySections(id, newId)
            _ <- appFormTables.duplicateApplicationForms(id, newId)
          } yield Some(newId)
      }
    }
    db.run(action.transactionally)
  }

  private def duplicateOpportunity(id: OpportunityId, opp: OpportunityRow): DBIO[OpportunityId] = {
    val newOpp = opp.copy(duplicatedFrom = Some(id), publishedAt = None)
    (opportunityTable returning opportunityTable.map(_.id)) += newOpp
  }

  private def duplicateOpportunitySections(oldId: OpportunityId, newId: OpportunityId): DBIO[Unit] = {
    sectionTable.filter(_.opportunityId === oldId).result.flatMap {
      sections =>
        (sectionTable ++= sections.map(_.copy(opportunityId = newId))).map(_ => ())
    }
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

  def sectionQ(id: Rep[OpportunityId], sectionNumber: Rep[Int]) = sectionTable.filter(s => s.opportunityId === id && s.sectionNumber === sectionNumber)

  lazy val sectionC = Compiled(sectionQ _)

  override def saveSectionDescription(id: OpportunityId, sectionNo: Int, description: Option[String]): Future[Int] = db.run {
    sectionQ(id, sectionNo).map(r=> r.text).update(description)
  }
}

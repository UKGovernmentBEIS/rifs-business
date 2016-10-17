package rifs.business.tables

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.ApplicationFormOps
import rifs.business.models.{ApplicationFormId, ApplicationFormRow, ApplicationFormSectionRow, OpportunityId}
import rifs.business.restmodels.{ApplicationForm, ApplicationFormSection}
import rifs.business.slicks.modules.{ApplicationFormModule, OpportunityModule}
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplicationFormFormTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ApplicationFormModule with OpportunityModule with DBBinding with ApplicationFormOps {

  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import driver.api._

  override def byId(id: ApplicationFormId): Future[Option[ApplicationForm]] = db.run {
    byIdWithSectionsC(id).result.map { rs =>
      val (as, ss) = rs.unzip
      as.map(a => ApplicationForm(a.id, a.opportunityId, sectionsFor(a, ss)))
    }.map(_.headOption)
  }

  override def forOpportunity(opportunityId: OpportunityId): Future[Option[ApplicationForm]] = db.run {
    applicationByOppIdC(opportunityId).result.map { rs =>
      val (as, ss) = rs.unzip
      as.map(a => ApplicationForm(a.id, a.opportunityId, sectionsFor(a, ss)))
    }.map(_.headOption)
  }

  def sectionsFor(applicationRow: ApplicationFormRow, sectionRows: Seq[ApplicationFormSectionRow]): Seq[ApplicationFormSection] = {
    sectionRows.filter(_.applicationId == applicationRow.id).map { s => ApplicationFormSection(s.sectionNumber, s.title, s.started) }
  }

  /*
    ******************************
    * Queries and compiled queries
     */
  def byIdQ(id: Rep[ApplicationFormId]): ApplicationQuery = for {a <- applicationFormTable if a.id == id} yield a

  val byIdC = Compiled(byIdQ _)

  type ApplicationWithSectionsJoin = Query[(ApplicationFormTable, ApplicationFormSectionTable), (ApplicationFormRow, ApplicationFormSectionRow), Seq]

  def byIdWithSectionsQ(id: Rep[ApplicationFormId]): ApplicationWithSectionsJoin = for {
    a <- applicationFormTable if a.id === id
    s <- applicationFormSectionTable if s.applicationFormId === a.id
  } yield (a, s)

  val byIdWithSectionsC = Compiled(byIdWithSectionsQ _)

  def applicationByOppIdQ(opportunityId: Rep[OpportunityId]): ApplicationWithSectionsJoin = for {
    a <- applicationFormTable if a.opportunityId === opportunityId
    s <- applicationFormSectionTable if s.applicationFormId === a.id
  } yield (a, s)

  val applicationByOppIdC = Compiled(applicationByOppIdQ _)
}

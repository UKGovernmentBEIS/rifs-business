package rifs.business.tables

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import rifs.business.data.ApplicationOps
import rifs.business.restmodels.{Application, ApplicationSection}
import rifs.models.{ApplicationId, ApplicationRow, OpportunityId}
import rifs.slicks.modules.{ApplicationModule, OpportunityModule}
import rifs.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplicationTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ApplicationModule with OpportunityModule with DBBinding with ApplicationOps {

  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import driver.api._

  createApplication.foreach(s => println(s"$s;"))

  override def byId(id: ApplicationId): Future[Option[ApplicationRow]] = db.run {
    applicationTable.filter(_.id === id).result.headOption
  }

  override def forOpportunity(opportunityId: OpportunityId): Future[Option[Application]] = db.run {
    val q = for {
      a <- applicationTable if a.opportunityId === opportunityId
      s <- applicationSectionTable if s.applicationId === a.id
    } yield (a, s)

    q.result.map {
      rs =>
        rs.groupBy(_._1).map { case (k, vs) => k -> vs.map(_._2) }.map {
          case (a, ss) => Application(a.id, a.opportunityId, ss.map(s => ApplicationSection(s.sectionNumber, s.title)))
        }
    }.map(_.headOption)
  }
}

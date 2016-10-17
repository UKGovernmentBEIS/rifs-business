package rifs.business.tables

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import com.github.tminglei.slickpg.{ExPostgresDriver, PgPlayJsonSupport}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormId, ApplicationId, ApplicationRow, ApplicationSectionRow}
import rifs.business.slicks.modules.{ApplicationFormModule, ApplicationModule, OpportunityModule}
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplicationTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ApplicationOps with ApplicationModule with DBBinding with ApplicationFormModule with OpportunityModule with ExPostgresDriver with PgPlayJsonSupport {
  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import PostgresAPI._

  override def byId(id: ApplicationId): Future[Option[ApplicationRow]] = db.run(applicationTable.filter(_.id === id).result.headOption)

  override def forApplicationForm(applicationFormId: ApplicationFormId): Future[Option[ApplicationRow]] = {
    val appFormF = db.run(applicationFormTable.filter(_.id === applicationFormId).result.headOption)
    val o = for {
      appForm <- OptionT(appFormF)
      app <- OptionT.liftF(fetchOrCreate(applicationFormId))
    } yield app

    o.value
  }

  private def fetchOrCreate(applicationFormId: ApplicationFormId): Future[ApplicationRow] = {
    db.run(applicationTable.filter(_.applicationFormId === applicationFormId).result.headOption).flatMap {
      case None => db.run((applicationTable returning applicationTable.map(_.id) into ((app, id) => app.copy(id=Some(id)))) += ApplicationRow(None, applicationFormId))
      case Some(app) => Future.successful(app)
    }
  }

  override def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]] = db.run {
    appSectionC(id, sectionNumber).result.headOption
  }

  override def saveSection(id: ApplicationId, sectionNumber: Int, answers: JsValue): Future[Int] = {
    fetchSection(id, sectionNumber).flatMap {
      case Some(row) => db.run(appSectionC(id, sectionNumber).update(row.copy(answers = answers)))
      case None => db.run(applicationSectionTable += ApplicationSectionRow(None, id, sectionNumber, answers))
    }
  }

  def appSectionQ(id: Rep[ApplicationId], sectionNumber: Rep[Int]) = applicationSectionTable.filter(a => a.applicationId === id && a.sectionNumber === sectionNumber)

  lazy val appSectionC = Compiled(appSectionQ _)


}

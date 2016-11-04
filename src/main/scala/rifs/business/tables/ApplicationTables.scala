package rifs.business.tables

import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import org.joda.time.LocalDateTime
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsObject
import rifs.business.controllers.JsonHelpers
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationFormId, ApplicationId, ApplicationRow, ApplicationSectionRow}
import rifs.business.restmodels.{Application, ApplicationSection}
import rifs.business.slicks.modules.{ApplicationFormModule, ApplicationModule, OpportunityModule}
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ApplicationTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ApplicationOps with ApplicationModule with DBBinding with ApplicationFormModule with OpportunityModule with ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda {
  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import PostgresAPI._

  override def byId(id: ApplicationId): Future[Option[ApplicationRow]] = db.run(applicationTable.filter(_.id === id).result.headOption)

  override def forForm(applicationFormId: ApplicationFormId): Future[Option[ApplicationRow]] = {
    val appFormF = db.run(applicationFormTable.filter(_.id === applicationFormId).result.headOption)

    for {
      _ <- OptionT(appFormF)
      app <- OptionT.liftF(fetchOrCreate(applicationFormId))
    } yield ApplicationRow(Some(app.id), app.applicationFormId)
  }.value

  override def application(applicationId: ApplicationId): Future[Option[Application]] = db.run {
    applicationWithSectionsC(applicationId).result
  }.map { ps =>
    val (as, ss) = ps.unzip
    as.map(a => buildApplication(a, ss.flatten)).headOption
  }

  def applicationWithSectionsQ(id: Rep[ApplicationId]) =
    (applicationTable joinLeft applicationSectionTable on (_.id === _.applicationId)).filter(_._1.id === id)

  val applicationWithSectionsC = Compiled(applicationWithSectionsQ _)

  def applicationWithSectionsForFormQ(id: Rep[ApplicationFormId]) =
    (applicationTable joinLeft applicationSectionTable on (_.id === _.applicationId)).filter(_._1.applicationFormId === id)

  val applicationWithSectionsForFormC = Compiled(applicationWithSectionsForFormQ _)

  private def fetchOrCreate(applicationFormId: ApplicationFormId): Future[Application] = {
    db.run(applicationWithSectionsForFormC(applicationFormId).result).flatMap {
      case Seq() => createApplicationForForm(applicationFormId).map { id => Application(id, applicationFormId, Seq()) }
      case ps =>
        val (as, ss) = ps.unzip
        Future.successful(as.map(a => buildApplication(a, ss.flatten)).head)
    }
  }

  private def buildApplication(app: ApplicationRow, secs: Seq[ApplicationSectionRow]): Application = {
    val sectionOverviews: Seq[ApplicationSection] = secs.map { s =>
      ApplicationSection(s.sectionNumber, s.answers, s.completedAt)
    }

    Application(app.id.get, app.applicationFormId, sectionOverviews)
  }

  private def createApplicationForForm(applicationFormId: ApplicationFormId): Future[ApplicationId] = db.run {
    (applicationTable returning applicationTable.map(_.id)) += ApplicationRow(None, applicationFormId)
  }

  override def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]] = db.run {
    appSectionC(id, sectionNumber).result.headOption
  }


  override def saveSection(id: ApplicationId, sectionNumber: Int, answers: JsObject, completedAt: Option[LocalDateTime] = None): Future[Int] = {

    fetchSection(id, sectionNumber).flatMap {
      case Some(row) =>
        preCheckSaveAnswers(row.answers, answers, completedAt) match {
          case true => db.run(appSectionC(id, sectionNumber).update(row.copy(answers = answers, completedAt = completedAt)))
          case false => Future.successful(1)
        }
      case None =>
        db.run(applicationSectionTable += ApplicationSectionRow(None, id, sectionNumber, answers, completedAt))
    }
  }

  override def deleteSection(id: ApplicationId, sectionNumber: Int): Future[Int] = db.run {
    appSectionC(id, sectionNumber).delete
  }

  def preCheckSaveAnswers(obj1: JsObject, obj2: JsObject, completedAt: Option[LocalDateTime]): Boolean = {

    !(JsonHelpers.flatten("", obj1).filter(_._2.isEmpty == false).toList.sortBy(_._2)
      .equals(JsonHelpers.flatten("", obj2).filter(_._2.isEmpty == false).toList.sortBy(_._2)) &&
      completedAt.isEmpty)
  }

  def appSectionQ(id: Rep[ApplicationId], sectionNumber: Rep[Int]) = applicationSectionTable.filter(a => a.applicationId === id && a.sectionNumber === sectionNumber)

  lazy val appSectionC = Compiled(appSectionQ _)

  val applicationSectionTableC = Compiled(applicationSectionTable.pack)

}

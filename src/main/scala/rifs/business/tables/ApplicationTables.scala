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
import rifs.business.models._
import rifs.business.restmodels.{Application, ApplicationSection}
import rifs.business.slicks.modules.{ApplicationFormModule, ApplicationModule, OpportunityModule}
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future, Promise}

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

  override def delete(id: ApplicationId): Future[Unit] = db.run {
    for {
      _ <- applicationSectionTable.filter(_.applicationId === id).delete
      _ <- applicationTable.filter(_.id === id).delete
    } yield ()
  }

  override def deleteAll: Future[Unit] = db.run {
    for {
      _ <- applicationSectionTable.delete
      _ <- applicationTable.delete
    } yield ()
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

  override def fetchAppWithSection(id: ApplicationId, sectionNumber: Int): Future[Option[(ApplicationRow, Option[ApplicationSectionRow])]] = db.run {
    appWithSectionC(id, sectionNumber).result.headOption
  }

  override def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]] = db.run {
    appSectionC(id, sectionNumber).result.headOption
  }

  override def fetchSections(id: ApplicationId): Future[Set[ApplicationSectionRow]] = db.run(appSectionsC(id).result).map(_.toSet)

  override def saveSection(id: ApplicationId, sectionNumber: Int, answers: JsObject, completedAt: Option[LocalDateTime] = None): Future[Int] = {
    fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, Some(section))) => areDifferent(section.answers, answers) || completedAt.isDefined match {
        case true => db.run(appSectionC(id, sectionNumber).update(section.copy(answers = answers, completedAt = completedAt)))
        case false => Future.successful(1)
      }
      case Some((app, None)) => db.run(applicationSectionTable += ApplicationSectionRow(None, id, sectionNumber, answers, completedAt))
      case None => Future.successful(0)
    }
  }

  def areDifferent(obj1: JsObject, obj2: JsObject): Boolean = {
    val flat1 = JsonHelpers.flatten("", obj1).filter { case (_, v) => v.trim != "" }
    val flat2 = JsonHelpers.flatten("", obj2).filter { case (_, v) => v.trim != "" }
    flat1 != flat2
  }

  def joinedAppWithSection(id: Rep[ApplicationId], sectionNumber: Rep[Int]) = for {
    as <- applicationTable joinLeft applicationSectionTable on ((a, s) => a.id === s.applicationId && s.sectionNumber === sectionNumber) if as._1.id === id
  } yield as

  def appWithSectionQ(id: Rep[ApplicationId], sectionNumber: Rep[Int]) = joinedAppWithSection(id, sectionNumber)

  lazy val appWithSectionC = Compiled(appWithSectionQ _)

  override def deleteSection(id: ApplicationId, sectionNumber: Int): Future[Int] = db.run {
    appSectionC(id, sectionNumber).delete
  }

  override def submit(id: ApplicationId): Future[SubmittedApplicationRef] = {
    // dummy method
    play.api.Logger.info(s"Dummy application submission for $id")
    byId(id).flatMap {
      case Some(appRow) => Future{ id }
      case None => Promise.failed(new NoSuchElementException("Application ID")).future
    }
  }

  def appSectionQ(id: Rep[ApplicationId], sectionNumber: Rep[Int]) = applicationSectionTable.filter(a => a.applicationId === id && a.sectionNumber === sectionNumber)

  lazy val appSectionC = Compiled(appSectionQ _)

  def appSectionsQ(id: Rep[ApplicationId]) = applicationSectionTable.filter(_.applicationId === id)

  lazy val appSectionsC = Compiled(appSectionsQ _)
}

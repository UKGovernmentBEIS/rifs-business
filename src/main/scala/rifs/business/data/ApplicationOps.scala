package rifs.business.data

import com.google.inject.ImplementedBy
import org.joda.time.LocalDateTime
import play.api.libs.json.JsObject
import rifs.business.models.{ApplicationFormId, ApplicationId, ApplicationRow, ApplicationSectionRow}
import rifs.business.restmodels.ApplicationOverview
import rifs.business.tables.ApplicationTables

import scala.concurrent.Future

@ImplementedBy(classOf[ApplicationTables])
trait ApplicationOps {
  def byId(id: ApplicationId): Future[Option[ApplicationRow]]

  def forForm(applicationFormId: ApplicationFormId): Future[Option[ApplicationRow]]

  def overview(applicationFormId: ApplicationId): Future[Option[ApplicationOverview]]

  def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]]

  def saveSection(id: ApplicationId, sectionNumber: Int, answers: JsObject, completedAt: Option[LocalDateTime] = None): Future[Int]

}

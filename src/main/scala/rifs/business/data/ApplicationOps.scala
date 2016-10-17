package rifs.business.data

import com.google.inject.ImplementedBy
import play.api.libs.json.JsValue
import rifs.business.models.{ApplicationFormId, ApplicationId, ApplicationRow, ApplicationSectionRow}
import rifs.business.tables.ApplicationTables

import scala.concurrent.Future

@ImplementedBy(classOf[ApplicationTables])
trait ApplicationOps {
  def byId(id: ApplicationId): Future[Option[ApplicationRow]]

  def forApplicationForm(applicationFormId: ApplicationFormId):Future[Option[ApplicationRow]]

  def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]]

  def saveSection(id:ApplicationId, sectionNumber:Int, answers:JsValue) : Future[Int]
}

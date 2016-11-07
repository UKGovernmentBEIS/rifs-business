package rifs.business.data

import com.google.inject.ImplementedBy
import org.joda.time.LocalDateTime
import play.api.libs.json.JsObject
import rifs.business.models.{ApplicationFormId, ApplicationId, ApplicationRow, ApplicationSectionRow}
import rifs.business.restmodels.Application
import rifs.business.tables.ApplicationTables

import scala.concurrent.Future

@ImplementedBy(classOf[ApplicationTables])
trait ApplicationOps {
  def byId(id: ApplicationId): Future[Option[ApplicationRow]]

  def delete(id: ApplicationId): Future[Unit]

  def forForm(applicationFormId: ApplicationFormId): Future[Option[ApplicationRow]]

  def application(applicationId: ApplicationId): Future[Option[Application]]

  /**
    * @return `Some[ApplicationSectionRow]` if the application with the given `id` was found and it had a
    *         section with number `sectionNumber`. If there is no section, or if there is no application with
    *         the given `id` then this returns `None`. If you need to know whether it was the application
    *         or the section that was missing then use `fetchAppWithSection`.
    */
  def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]]

  /**
    * @return an option of a pair where the first element is the application row and the second element
    *         is an option of the section row. If the application with `id` does not exist then the
    *         whole result will be `None`, and if the application exists but there is no section with
    *         the given `sectionNumber` then the overall result will be `Some`, but the second element of
    *         the pair will be `None`. This way the caller can tell of the select failed because the
    *         application didn't exist, or because the application exists but the section doesn't.
    */
  def fetchAppWithSection(id: ApplicationId, sectionNumber: Int): Future[Option[(ApplicationRow, Option[ApplicationSectionRow])]]

  def fetchSections(id: ApplicationId): Future[Set[ApplicationSectionRow]]

  def saveSection(id: ApplicationId, sectionNumber: Int, answers: JsObject, completedAt: Option[LocalDateTime] = None): Future[Int]

  def deleteSection(id: ApplicationId, sectionNumber: Int): Future[Int]

}

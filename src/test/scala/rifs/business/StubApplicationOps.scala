/*
 * Copyright (C) 2016  Department for Business, Energy and Industrial Strategy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rifs.business

import org.joda.time.DateTime
import play.api.libs.json.JsObject
import rifs.business.data.{ApplicationDetails, ApplicationOps}
import rifs.business.models._
import rifs.business.restmodels.Application

import scala.concurrent.Future

class StubApplicationOps extends ApplicationOps{
  override def byId(id: ApplicationId): Future[Option[ApplicationRow]] = ???

  override def gatherDetails(id: ApplicationId): Future[Option[ApplicationDetails]] = ???

  override def delete(id: ApplicationId): Future[Unit] = ???

  override def deleteAll: Future[Unit] = ???

  override def forForm(applicationFormId: ApplicationFormId): Future[Option[ApplicationRow]] = ???

  override def application(applicationId: ApplicationId): Future[Option[Application]] = ???

  override def fetchSection(id: ApplicationId, sectionNumber: Int): Future[Option[ApplicationSectionRow]] = ???

  override def fetchAppWithSection(id: ApplicationId, sectionNumber: Int): Future[Option[(ApplicationRow, Option[ApplicationSectionRow])]] = ???

  override def fetchSections(id: ApplicationId): Future[Set[ApplicationSectionRow]] = ???

  override def saveSection(id: ApplicationId, sectionNumber: Int, answers: JsObject, completedAt: Option[DateTime]): Future[Int] = ???

  override def deleteSection(id: ApplicationId, sectionNumber: Int): Future[Int] = ???

  override def submit(id: ApplicationId): Future[Option[SubmittedApplicationRef]] = ???

  override def clearSectionCompletedDate(id: SubmittedApplicationRef, sectionNumber: Int): Future[Int] = ???

  override def updatePersonalReference(id: SubmittedApplicationRef, reference: Option[String]) = ???
}

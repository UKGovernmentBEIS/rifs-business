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

package rifs.business.actions

import javax.inject.Inject

import play.api.mvc.Results._
import play.api.mvc._
import rifs.business.data.ApplicationOps
import rifs.business.models.{ApplicationId, ApplicationRow, ApplicationSectionRow}

import scala.concurrent.{ExecutionContext, Future}

case class AppSectionRequest[A](app: ApplicationRow, appSection: Option[ApplicationSectionRow], request: Request[A]) extends WrappedRequest[A](request)

class AppSectionAction @Inject()(applications: ApplicationOps)(implicit ec: ExecutionContext) {
  def apply(id: ApplicationId, sectionNum: Int): ActionBuilder[AppSectionRequest] =
    new ActionBuilder[AppSectionRequest] {
      override def invokeBlock[A](request: Request[A], next: (AppSectionRequest[A]) => Future[Result]): Future[Result] = {
        applications.fetchAppWithSection(id, sectionNum).flatMap {
          case Some((app, maybeSectionRow)) => next(AppSectionRequest(app, maybeSectionRow, request))
          case None => Future.successful(NotFound(s"No application with id ${id.id}"))
        }
      }
    }
}
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
package rifs.business.actions

import javax.inject.Inject

import play.api.mvc.Results._
import play.api.mvc._
import rifs.business.data.ApplicationOps
import rifs.business.models.ApplicationId
import rifs.business.restmodels.Application

import scala.concurrent.{ExecutionContext, Future}

case class ApplicationRequest[A](Application: Application, request: Request[A]) extends WrappedRequest[A](request)

class ApplicationAction @Inject()(opportunities: ApplicationOps)(implicit ec: ExecutionContext) {
  def apply(id: ApplicationId): ActionBuilder[ApplicationRequest] =
    new ActionBuilder[ApplicationRequest] {
      override def invokeBlock[A](request: Request[A], next: (ApplicationRequest[A]) => Future[Result]): Future[Result] = {
        opportunities.application(id).flatMap {
          case Some(opp) => next(ApplicationRequest(opp, request))
          case None => Future.successful(NotFound(s"No application with id ${id.id} exists"))
        }
      }
    }
}
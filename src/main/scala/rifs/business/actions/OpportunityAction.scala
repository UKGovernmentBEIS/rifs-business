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
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId
import rifs.business.restmodels.Opportunity

import scala.concurrent.{ExecutionContext, Future}

case class OpportunityRequest[A](opportunity: Opportunity, request: Request[A]) extends WrappedRequest[A](request)

class OpportunityAction @Inject()(opportunities: OpportunityOps)(implicit ec:ExecutionContext) {
  def apply(id: OpportunityId): ActionBuilder[OpportunityRequest] =
    new ActionBuilder[OpportunityRequest] {
      override def invokeBlock[A](request: Request[A], next: (OpportunityRequest[A]) => Future[Result]): Future[Result] = {
        opportunities.opportunity(id).flatMap {
          case Some(opp) => next(OpportunityRequest(opp, request))
          case None => Future.successful(NotFound(s"No opportunity with id ${id.id} exists"))
        }
      }
    }
}
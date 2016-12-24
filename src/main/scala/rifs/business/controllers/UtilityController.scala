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

package rifs.business.controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import rifs.business.actions.OpportunityAction
import rifs.business.data.{ApplicationOps, OpportunityOps}

import scala.concurrent.ExecutionContext

class UtilityController @Inject()(applications: ApplicationOps, opportunities: OpportunityOps, OpportunityAction: OpportunityAction)(implicit val ec: ExecutionContext) extends Controller {

  def reset() = Action.async {
    for {
      _ <- applications.deleteAll
      _ <- opportunities.reset()
    } yield NoContent
  }
}

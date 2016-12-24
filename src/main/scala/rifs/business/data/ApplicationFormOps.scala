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

package rifs.business.data

import com.google.inject.ImplementedBy
import rifs.business.models.{ApplicationFormId, OpportunityId}
import rifs.business.restmodels.ApplicationForm
import rifs.business.tables.ApplicationFormTables

import scala.concurrent.Future

@ImplementedBy(classOf[ApplicationFormTables])
trait ApplicationFormOps {
  def byId(id: ApplicationFormId): Future[Option[ApplicationForm]]

  def forOpportunity(opportunityId: OpportunityId): Future[Option[ApplicationForm]]
}

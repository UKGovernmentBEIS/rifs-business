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

import play.api.libs.json.Json

case class ErrorResult(errorKey: String, arguments: Seq[String], fieldName: Option[String] = None,
                       fieldRejectedValue: Option[String] = None, details: Option[String] = None)

object ErrorResult {
  implicit val errorFormat = Json.format[ErrorResult]

  def apply(errorKey: String, status: Int): ErrorResult = ErrorResult(errorKey, Seq("", "", status.toString))

  def apply(error: RuntimeException): ErrorResult = {
    ErrorResult(error.getClass.getName, Nil, None, None, Some(error.getMessage))
  }
}

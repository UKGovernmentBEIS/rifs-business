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

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object JsonHelpers {
  def flatten(name: String, o: JsObject): Map[String, String] = {

    def subName(n: String) = if (name == "") n else s"$name.$n"

    import cats.implicits._
    o.fields.map {
      case (n, jo: JsObject) => flatten(subName(n), jo)
      case (n, JsString(s)) => Map(subName(n) -> s)
      // HACK: for the moment treat an array as a big string
      case (n, JsArray(values)) => Map(subName(n) -> values.toString())
      // For the moment any non-string value gets dropped
      case (n, _) => Map[String, String]()
    }.fold(Map[String, String]())(_ combine _)
  }
}

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

package rifs.business.tables

import play.api.libs.json._

case class Join[T1, T2](primary: T1, secondary: T2, joinName: String)

object Join {
  implicit def writes[T1, T2](implicit w1: Writes[T1], w2: Writes[T2]) = new Writes[Join[T1, T2]] {
    override def writes(j: Join[T1, T2]): JsValue = {
      val o1 = Json.toJson(j.primary).as[JsObject]
      val o2 = Json.toJson(j.secondary)

      o1 + (j.joinName -> o2)
    }
  }
}

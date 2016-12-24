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

import play.api.libs.json.{JsObject, Json, Writes}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait ControllerUtils {
  self: Controller =>

  implicit def ec: ExecutionContext

  def jsonResult[T: Writes](t: Option[T]) = t match {
    case None => NotFound(JsObject(Seq("errors" -> Json.toJson(Seq(ErrorResult("GENERAL_NOT_FOUND", Seq("", "", "404")))))))
    case Some(result) => Ok(Json.toJson(result))
  }

  def jsonResult[T](t: Future[T])(implicit w: Writes[T]) =  {
    t.map {result => Ok( Json.toJson(result) )}
      .recover {
      case nf: NoSuchElementException =>
        NotFound(JsObject(Seq("errors" -> Json.toJson(Seq(ErrorResult(nf.getMessage, Seq("", "", "404")))))))
    }
  }
}

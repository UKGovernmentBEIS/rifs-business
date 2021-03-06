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

import play.api.libs.json._
import play.api.mvc.Controller
import rifs.business.actions.AppSectionAction
import rifs.business.data.ApplicationOps
import rifs.business.models.ApplicationId

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ItemController @Inject()(applications: ApplicationOps, AppSectionAction: AppSectionAction)
                              (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def delete(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = AppSectionAction(id, sectionNumber).async { implicit request =>
    val doc = request.appSection.map(_.answers).getOrElse(JsObject(Seq()))
    val items = doc \ "items" match {
      case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
      case _ => Seq()
    }
    val remainingItems = items.filterNot(o => hasItemNumber(o, itemNumber))
    val updated = doc + ("items" -> JsArray(remainingItems))
    applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)
  }

  def get(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = AppSectionAction(id, sectionNumber) { implicit request =>
    val doc = request.appSection.map(_.answers).getOrElse(JsObject(Seq()))
    jsonResult(findItem(doc, itemNumber))
  }

  private def hasItemNumber(o: JsObject, num: Int) = o \ "itemNumber" match {
    case JsDefined(JsNumber(n)) if n == num => true
    case _ => false
  }

  private def findItem(doc: JsObject, itemNumber: Int) = {
    val items = doc \ "items" match {
      case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
      case _ => Seq()
    }
    items.find(o => hasItemNumber(o, itemNumber))
  }

  def put(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = AppSectionAction(id, sectionNumber).async(parse.json[JsObject]) { implicit request =>
    def hasItemNumber(o: JsObject, num: Int) = o \ "itemNumber" match {
      case JsDefined(JsNumber(n)) if n == num => true
      case _ => false
    }

    val doc = request.appSection.map(_.answers).getOrElse(JsObject(Seq()))
    val items = doc \ "items" match {
      case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
      case _ => Seq()
    }
    val remainingItems = items.filterNot(o => hasItemNumber(o, itemNumber))
    val newItem = request.body + ("itemNumber" -> JsNumber(itemNumber))
    val updated = doc + ("items" -> JsArray(newItem +: remainingItems))
    applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)
  }

  def post(id: ApplicationId, sectionNumber: Int) = AppSectionAction(id, sectionNumber).async(parse.json[JsObject]) { implicit request =>
    val doc = request.appSection.map(_.answers).getOrElse(JsObject(Seq()))

    val items: Seq[JsValue] = doc \ "items" match {
      case JsDefined(JsArray(is)) => is
      case _ => Seq()
    }

    allocateItemNumber().flatMap { n =>
      val newItem = request.body + ("itemNumber" -> JsNumber(n))
      val updated = doc + ("items" -> JsArray(newItem +: items))

      applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)
    }
  }

  /**
    * TODO: use a db sequence for this
    *
    * @return
    */
  def allocateItemNumber(): Future[Int] = Future {
    Random.nextInt().abs
  }
}

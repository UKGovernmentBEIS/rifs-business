package rifs.business.controllers

import javax.inject.Inject

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import rifs.business.data.ApplicationOps
import rifs.business.models.ApplicationId

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ItemController @Inject()(applications: ApplicationOps)
                              (implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def delete(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async { implicit request =>
    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))
        val items = doc \ "items" match {
          case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
          case _ => Seq()
        }
        val remainingItems = items.filterNot(o => hasItemNumber(o, itemNumber))
        val updated = doc + ("items" -> JsArray(remainingItems))
        applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)

      case None => Future.successful(NotFound)
    }
  }

  def get(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async { implicit request =>
    applications.fetchAppWithSection(id, sectionNumber).map {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))
        jsonResult(findItem(doc, itemNumber))

      case None => NotFound
    }
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

  def put(id: ApplicationId, sectionNumber: Int, itemNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    def hasItemNumber(o: JsObject, num: Int) = o \ "itemNumber" match {
      case JsDefined(JsNumber(n)) if n == num => true
      case _ => false
    }

    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))
        val items = doc \ "items" match {
          case JsDefined(JsArray(is)) => is.collect { case o: JsObject => o }
          case _ => Seq()
        }
        val remainingItems = items.filterNot(o => hasItemNumber(o, itemNumber))
        val newItem = request.body + ("itemNumber" -> JsNumber(itemNumber))
        val updated = doc + ("items" -> JsArray(newItem +: remainingItems))
        applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)

      case None => Future.successful(NotFound)
    }
  }

  def post(id: ApplicationId, sectionNumber: Int) = Action.async(parse.json[JsObject]) { implicit request =>
    applications.fetchAppWithSection(id, sectionNumber).flatMap {
      case Some((app, os)) =>
        val doc = os.map(_.answers).getOrElse(JsObject(Seq()))

        val items: Seq[JsValue] = doc \ "items" match {
          case JsDefined(JsArray(is)) => is
          case _ => Seq()
        }

        allocateItemNumber().flatMap { n =>
          val newItem = request.body + ("itemNumber" -> JsNumber(n))
          val updated = doc + ("items" -> JsArray(newItem +: items))

          applications.saveSection(id, sectionNumber, updated).map(_ => NoContent)
        }

      case None => Future.successful(NotFound)
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

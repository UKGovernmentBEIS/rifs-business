package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json.JsObject
import play.api.mvc.{Action, Controller}
import rifs.business.models.KeystoreId

import scala.concurrent.ExecutionContext

class KeystoreController @Inject()(val cached: Cached)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def get(id: KeystoreId) = Action.async { request => ??? }

  def delete(id: KeystoreId) = Action.async { request => ??? }

  /*
  * ttl is the maximum number of seconds the document should be stored
   */
  def put(id: KeystoreId, ttl: Long) = Action.async(parse.json[JsObject]) { request => ??? }

}

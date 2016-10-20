package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json.JsObject
import play.api.mvc.{Action, Controller}
import rifs.business.data.KeystoreId

import scala.concurrent.ExecutionContext

class KeystoreController @Inject()(val cached: Cached)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def get(id: KeystoreId) = Action.async { request => ??? }

  def delete(id: KeystoreId) = Action.async { request => ??? }

  def put(id: KeystoreId, ttl: String) = Action.async(parse.json[JsObject]) { request => ??? }

}

package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, Controller}
import rifs.business.data.KeystoreOps
import rifs.business.models.KeystoreId

import scala.concurrent.ExecutionContext

class KeystoreController @Inject()(val cached: Cached, keystore: KeystoreOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def get(id: KeystoreId) = Action.async {
    keystore.byId(id).map {
      case Some(k) => Ok(k.document)
      case None => NotFound
    }
  }

  val defaultTtlInMinutes = 60
  val sevenDaysInMinutes = 10080

  /*
  * ttl is the maximum number of seconds the document should be stored
   */
  def put(ttlInMinutes: Option[Int]) = Action.async(parse.json[JsObject]) { request =>
    keystore.put(request.body, ttlInMinutes.getOrElse(defaultTtlInMinutes).max(sevenDaysInMinutes)).map { kid =>
      Created(JsString(kid.id))
    }
  }
}

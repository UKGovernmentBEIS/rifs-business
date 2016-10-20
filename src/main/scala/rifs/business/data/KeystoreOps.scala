package rifs.business.data

import play.api.libs.json.JsObject
import rifs.business.models.KeystoreId

import scala.concurrent.Future

trait KeystoreOps {
  def get(id: KeystoreId): Future[Option[JsObject]]

  def put(doc: JsObject, ttl: Long): Future[KeystoreId]

  def delete(id: KeystoreId): Future[Unit]
}

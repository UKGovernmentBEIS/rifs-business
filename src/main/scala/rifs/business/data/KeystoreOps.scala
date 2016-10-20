package rifs.business.data

import play.api.libs.json.JsObject

import scala.concurrent.Future
import scala.concurrent.duration.Duration

case class KeystoreId(id: String) extends AnyVal

trait KeystoreOps {
  def get(id: KeystoreId): Future[Option[JsObject]]

  def put(doc: JsObject, ttl: Duration): Future[KeystoreId]

  def delete(id: KeystoreId): Future[Unit]
}

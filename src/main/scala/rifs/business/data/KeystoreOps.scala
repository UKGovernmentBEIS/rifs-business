package rifs.business.data

import com.google.inject.ImplementedBy
import play.api.libs.json.JsObject
import rifs.business.models.{KeystoreId, KeystoreRow}
import rifs.business.tables.KeystoreTables

import scala.concurrent.Future

@ImplementedBy(classOf[KeystoreTables])
trait KeystoreOps {
  /**
    * Find an entry by Id, but ignore any expired rows
    */
  def byId(id: KeystoreId): Future[Option[KeystoreRow]]

  def put(doc: JsObject, ttl: Int): Future[KeystoreId]
}

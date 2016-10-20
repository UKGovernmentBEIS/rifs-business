package rifs.business.models

import org.joda.time.LocalDateTime
import play.api.libs.json.JsObject

case class KeystoreId(id: String) extends AnyVal

case class KeystoreRow(id: KeystoreId, expiry: LocalDateTime, doc: JsObject)

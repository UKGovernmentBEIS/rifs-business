package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import org.joda.time.LocalDateTime
import play.api.libs.json.{JsObject, Json}
import rifs.business.models.{KeystoreId, KeystoreRow}
import rifs.business.slicks.support.DBBinding
import rifs.slicks.gen.IdType
import slick.jdbc.JdbcType
import slick.lifted.Rep

trait KeystoreModule {
  self:  ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda with DBBinding =>

  object PostgresAPI extends API with JsonImplicits with JodaDateTimeImplicits

  override val pgjson = "jsonb"

  implicit val playJsonTypeMapper: JdbcType[JsObject] =
    new GenericJdbcType[JsObject](
      pgjson,
      (v) => Json.parse(v).as[JsObject],
      (v) => Json.stringify(v),
      zero = JsObject(Seq()),
      hasLiteralForm = false
    )

  implicit def playJsonColumnExtensionMethods(c: Rep[JsObject]): JsonColumnExtensionMethods[JsObject, JsObject] = {
    new JsonColumnExtensionMethods[JsObject, JsObject](c)
  }
  implicit def playJsonOptionColumnExtensionMethods(c: Rep[Option[JsObject]]): JsonColumnExtensionMethods[JsObject, Option[JsObject]] = {
    new JsonColumnExtensionMethods[JsObject, Option[JsObject]](c)
  }

  import PostgresAPI._

  implicit def KeystoreIdMapper: BaseColumnType[KeystoreId] = MappedColumnType.base[KeystoreId, String](_.id, KeystoreId)

  type KeystoreQuery = Query[KeystoreTable, KeystoreRow, Seq]
  class KeystoreTable(tag: Tag) extends Table[KeystoreRow](tag, "keystore") {
    def id = column[KeystoreId]("id", O.Length(IdType.length), O.PrimaryKey)
    def expiry = column[LocalDateTime]("expiry")
    def doc = column[JsObject]("doc")
    def * = (id, expiry, doc) <> (KeystoreRow.tupled, KeystoreRow.unapply)
  }
  lazy val keystoreTable = TableQuery[KeystoreTable]
}
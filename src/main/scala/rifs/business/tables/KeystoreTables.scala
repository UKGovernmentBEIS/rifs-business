package rifs.business.tables

import java.util.UUID
import javax.inject.Inject

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import org.joda.time.LocalDateTime
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsObject
import rifs.business.data.KeystoreOps
import rifs.business.models.{KeystoreId, KeystoreRow}
import rifs.business.slicks.modules.KeystoreModule
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class KeystoreTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends KeystoreModule with ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda with DBBinding with KeystoreOps {
  override def dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import PostgresAPI._


  override def byId(id: KeystoreId): Future[Option[KeystoreRow]] = db.run {
    keystoreTable.filter(k => k.id === id && k.expiry >= LocalDateTime.now).result.headOption
  }

  override def put(doc: JsObject, ttl: Int): Future[KeystoreId] = {
    val expiryDate = LocalDateTime.now().plusMinutes(ttl)
    val id = KeystoreId(UUID.randomUUID().toString)

    db.run {
      (keystoreTable returning keystoreTable.map(_.id)) += KeystoreRow(id, expiryDate, doc)
    }
  }
}

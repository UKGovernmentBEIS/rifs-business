package rifs.business.tables

import javax.inject.Inject

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsObject
import rifs.business.data.KeystoreOps
import rifs.business.models.KeystoreId
import rifs.business.slicks.modules.KeystoreModule
import rifs.business.slicks.support.DBBinding
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import org.joda.time.LocalDateTime

import scala.concurrent.{ExecutionContext, Future}

class KeystoreTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends KeystoreModule with  ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda with DBBinding with KeystoreOps {
  override def dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import PostgresAPI._

  override def get(id: KeystoreId): Future[Option[JsObject]] = db.run {
    keystoreTable.filter(k => k.id === id && k.expiry >= LocalDateTime.now).map(_.doc).result.headOption
  }

  override def put(doc: JsObject, ttl: Long): Future[KeystoreId] = ???

  override def delete(id: KeystoreId): Future[Unit] = ???
}

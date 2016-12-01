package rifs.business.slicks.support

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig

trait DBBinding extends ExPostgresDriver with PgDateSupportJoda {

  def dbConfigProvider: DatabaseConfigProvider

  lazy val dbConfig: DatabaseConfig[ExPostgresDriver] = dbConfigProvider.get[ExPostgresDriver]

  lazy val driver = new ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda {
    override val pgjson = "jsonb"
  }

  lazy val db: driver.api.Database = dbConfig.db

  override val api = new API with DateTimeImplicits

}

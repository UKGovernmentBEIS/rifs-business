package rifs.business.slicks.support

import slick.backend.DatabaseConfig
import slick.driver.{JdbcProfile, PostgresDriver}

trait DBBinding {
  def dbConfig: DatabaseConfig[JdbcProfile]

  lazy val driver = PostgresDriver

  lazy val db: driver.api.Database = dbConfig.db
}

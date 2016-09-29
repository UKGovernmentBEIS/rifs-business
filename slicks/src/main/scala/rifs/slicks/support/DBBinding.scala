package rifs.slicks.support

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait DBBinding {
  def dbConfig: DatabaseConfig[JdbcProfile]

  lazy val db: driver.api.Database = dbConfig.db

  lazy val driver = dbConfig.driver
}

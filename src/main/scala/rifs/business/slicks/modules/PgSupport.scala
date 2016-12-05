package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgDateSupportJoda, PgPlayJsonSupport}

trait PgSupport extends ExPostgresDriver with PgPlayJsonSupport with PgDateSupportJoda{
  override def pgjson: String = "jsonb"
}

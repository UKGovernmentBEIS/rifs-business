package rifs.business.slicks.modules

import com.github.tminglei.slickpg.{ExPostgresDriver, PgPlayJsonSupport}
import play.api.libs.json.{JsArray, JsObject, Json}
import slick.jdbc.JdbcType

trait PlayJsonMappers {
  self: ExPostgresDriver with PgPlayJsonSupport =>

  implicit val playJsObjectTypeMapper: JdbcType[JsObject] =
    new GenericJdbcType[JsObject](
      pgjson,
      (v) => Json.parse(v).as[JsObject],
      (v) => Json.stringify(v),
      zero = JsObject(Seq()),
      hasLiteralForm = false
    )

  implicit val playJsArrayTypeMapper: JdbcType[JsArray] =
    new GenericJdbcType[JsArray](
      pgjson,
      (v) => Json.parse(v).as[JsArray],
      (v) => Json.stringify(v),
      zero = JsArray(),
      hasLiteralForm = false
    )
}


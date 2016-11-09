package rifs.business.controllers

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object JsonHelpers {
  def flatten(name: String, o: JsObject): Map[String, String] = {

    def subName(n: String) = if (name == "") n else s"$name.$n"

    import cats.implicits._
    o.fields.map {
      case (n, jo: JsObject) => flatten(subName(n), jo)
      case (n, JsString(s)) => Map(subName(n) -> s)
      // HACK: for the moment treat an array as a big string
      case (n, JsArray(values)) => Map(subName(n) -> values.toString())
      // For the moment any non-string value gets dropped
      case (n, _) => Map[String, String]()
    }.fold(Map[String, String]())(_ combine _)
  }

  def try2JSon[T](v: Try[T])(implicit w: Writes[T]): JsValue = {
      v match {
        case Success(x) =>
           JsObject( Seq( ("value", w.writes(x))) )
        case Failure(ex) =>
            val exJs = JsObject( Seq( ("type", JsString(ex.getClass.getName)), ("message", JsString(ex.getMessage)) ) )
            JsObject( Seq( ("error", exJs )) )
      }
  }
}

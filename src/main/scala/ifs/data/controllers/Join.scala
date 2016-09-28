package ifs.data.controllers

import play.api.libs.json._

case class Join[T1, T2](primary: T1, secondary: T2, joinName: String)

object Join {
  implicit def writes[T1, T2](implicit w1: Writes[T1], w2: Writes[T2]) = new Writes[Join[T1, T2]] {
    override def writes(j: Join[T1, T2]): JsValue = {
      val o1 = Json.toJson(j.primary).as[JsObject]
      val o2 = Json.toJson(j.secondary)

      o1 + (j.joinName -> o2)
    }
  }
}

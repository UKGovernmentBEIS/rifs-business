package rifs.business.controllers

import play.api.libs.json.{JsObject, Json, Writes}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait ControllerUtils {
  self: Controller =>

  implicit def ec: ExecutionContext

  def jsonResult[T: Writes](t: Option[T]) = t match {
    case None => NotFound(JsObject(Seq("errors" -> Json.toJson(Seq(ErrorResult("GENERAL_NOT_FOUND", Seq("", "", "404")))))))
    case Some(result) => Ok(Json.toJson(result))
  }

  def jsonResult[T](t: Future[T])(implicit w: Writes[T]) =  {
    t.map {result => Ok( Json.toJson(result) )}
      .recover {
      case nf: NoSuchElementException =>
        NotFound(JsObject(Seq("errors" -> Json.toJson(Seq(ErrorResult(nf.getMessage, Seq("", "", "404")))))))
    }
  }
}

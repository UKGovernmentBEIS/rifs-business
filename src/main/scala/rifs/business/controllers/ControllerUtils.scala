package rifs.business.controllers

import play.api.cache.Cached
import play.api.libs.json.{JsObject, Json, Writes}
import play.api.mvc._
import rifs.business.Config

import scala.concurrent.ExecutionContext


trait ControllerUtils {
  self: Controller =>

  val cacheTimeout = Config.config.cache.timeout.getOrElse(10)

  def cached: Cached

  implicit def ec: ExecutionContext

  def jsonResult[T: Writes](t: Option[T]) = t match {
    case None => NotFound(JsObject(Seq("errors" -> Json.toJson(Seq(ErrorResult("GENERAL_NOT_FOUND", Seq("", "", "404")))))))
    case Some(result) => Ok(Json.toJson(result))
  }

  def cacheOk[T](action: Action[T]) =
    if (cacheTimeout > 0) cached.status(rh => rh.uri, 200, cacheTimeout)(action)
    else action
}

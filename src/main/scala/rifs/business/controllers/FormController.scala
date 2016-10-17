package rifs.business.controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.mvc.{Action, Controller}
import rifs.business.forms._

import scala.concurrent.ExecutionContext

case class FormId(id: Long)

class FormController @Inject()(val cached: Cached)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def save(id: FormId, answers: Answers) = Action { implicit request =>
    handleEvent(Seq(), Save(answers))
    Accepted
  }
}

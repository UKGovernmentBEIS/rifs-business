package rifs.business.controllers.manage

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import rifs.business.controllers.ControllerUtils
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId
import rifs.business.restmodels.OpportunityDescriptionSection

import scala.concurrent.ExecutionContext

class OpportunityController @Inject()(val cached: Cached, opportunities: OpportunityOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  implicit val descrSectFmt = Json.format[(Int, Option[String])]

  def saveDescription(id: OpportunityId) = Action.async(parse.json(descrSectFmt)) { implicit request =>
    val description = request.body
    opportunities.saveSectionDescription(id, description._1, description._2).map {
      case 0 => NotFound
      case _ => Ok("")
    }.recover {
      case e => BadRequest(s" Error: $e")
    }
  }
}

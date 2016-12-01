package rifs.business.controllers.manage

import javax.inject.Inject

import play.api.cache.Cached
import play.api.libs.json.JsString
import play.api.mvc.{Action, Controller}
import rifs.business.controllers.ControllerUtils
import rifs.business.data.OpportunityOps
import rifs.business.models.OpportunityId

import scala.concurrent.ExecutionContext

class OpportunityController @Inject()(val cached: Cached, opportunities: OpportunityOps)(implicit val ec: ExecutionContext) extends Controller with ControllerUtils {

  def saveDescription(id: OpportunityId, sectionNum: Int) = Action.async(parse.json[Option[JsString]]) { implicit request =>
    val description = request.body.map(_.value)

    opportunities.saveSectionDescription(id, sectionNum, description).map {
      case 0 => NotFound
      case _ => NoContent
    }
  }
}

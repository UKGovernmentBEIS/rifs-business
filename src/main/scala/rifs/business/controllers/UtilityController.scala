package rifs.business.controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import rifs.business.actions.OpportunityAction
import rifs.business.data.{ApplicationOps, OpportunityOps}

import scala.concurrent.ExecutionContext

class UtilityController @Inject()(applications: ApplicationOps, opportunities: OpportunityOps, OpportunityAction: OpportunityAction)(implicit val ec: ExecutionContext) extends Controller {

  def reset() = Action.async {
    for {
      _ <- applications.deleteAll
      _ <- opportunities.reset()
    } yield NoContent
  }
}

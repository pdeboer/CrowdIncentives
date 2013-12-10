package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.U

/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object Global extends Controller {
  def list() = Action {
    implicit request =>
    Ok(views.html.global(List.empty[IntegratedStory],
      IndexData(U.user(session))))
  }
}

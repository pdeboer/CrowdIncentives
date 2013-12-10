package controllers

import play.api.mvc.{Action, Controller}

/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object Global extends Controller {
  def list() = Action {
    Ok(views.html.global(List.empty[IntegratedStory]))
  }
}

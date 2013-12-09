package controllers

import play.api.mvc.{Controller, Action}

/**
 * @author pdeboer
 *         First created on 09/12/13 at 17:38
 */
object Login extends Controller {
  def login(name: String, password: String) = Action {

    Ok(views.html.index("ok"))
  }
}

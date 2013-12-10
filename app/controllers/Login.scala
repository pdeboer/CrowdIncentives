package controllers

import _root_.dal.LoginDAL
import play.api.mvc.{Controller, Action}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._

/**
 * @author pdeboer
 *         First created on 09/12/13 at 17:38
 */
object Login extends Controller {
  def login() = Action {
    implicit request =>
      val map = request.body.asFormUrlEncoded.getOrElse(null)
      val user = map.get("username").get.headOption.getOrElse(null)
      val pw = map.get("password").get.headOption.getOrElse(null)

      if (map == null)
        Ok(views.html.login())
      else if (new LoginDAL().checkLogin(UserData(user, pw)))
        Ok(views.html.index("ok")).withSession("user" -> user)
      else NotFound(views.html.login("Login not successful"))
  }
}
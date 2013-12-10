package controllers

import controllers.dal.UserDAL
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
      if (map == null)
        Ok(views.html.login())
      else {
        val user = map.get("username").get.headOption.getOrElse(null)
        val pw = map.get("password").get.headOption.getOrElse(null)

        val userId = new UserDAL().checkLogin(user, pw)


        if (userId > 0)
          Redirect("/").withSession("user" -> userId.toString)
        else NotFound(views.html.login("Login not successful"))
      }
  }
}
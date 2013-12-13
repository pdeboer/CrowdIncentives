package controllers

import controllers.dal.UserDAL
import play.api.mvc.{Controller, Action}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import controllers.utils.U

/**
 * @author pdeboer
 *         First created on 09/12/13 at 17:38
 */
object Login extends Controller {
  def login() = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
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

  def register() = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val map = request.body.asFormUrlEncoded.getOrElse(null)
        if (map == null) {
          Ok(views.html.register(""))
        } else {
          val user = map.get("username").get.headOption.getOrElse(null)
          val pw = map.get("password").get.headOption.getOrElse(null)
          val code = map.get("code").get.headOption.getOrElse(null)

          val userDAL: UserDAL = new UserDAL()
          if (userDAL.register(user, pw, code)) {
            Redirect("/").withSession("user" -> userDAL.checkLogin(user, pw).toString)
          } else {
            Ok(views.html.register("Registration not successful. Either your code is invalid/already used or your username is not available any more."))
          }
        }
      }
  }

  def waitingroom() = Action {
    implicit request =>
      Ok(views.html.waitingroom(new UserDAL().userRoundTimeframe(U.user(session))))
  }
}
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
  val userForm = Form(
    mapping(
      "name" -> nonEmptyText(8, 50),
      "password" -> nonEmptyText(8, 50)
    )(UserData.apply)(UserData.unapply)
  )

  def login() = Action { implicit request =>
    if(userForm.value.isEmpty)
      Ok(views.html.login())
    else if (new LoginDAL().checkLogin(userForm.get))
      Ok(views.html.index("ok"))
    else NotFound(views.html.login("Login not successful"))
  }
}

case class UserData(name:String, password:String)

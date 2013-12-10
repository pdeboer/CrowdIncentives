package controllers

import play.api._
import play.api.mvc._
import controllers.dal.TemplateDAL

object Application extends Controller {
  def index = Action {
    implicit request =>
      if (session.get("user").isEmpty)
        Redirect("/login")
      else {
        //get parts
        Ok(views.html.index(session.get("user").get,
          new TemplateDAL().getParts()))
      }
  }

}

case class UserData(name: String, password: String)
case class TemplatePart (id:Long, name:String, beforeText:String="", afterText:String="")

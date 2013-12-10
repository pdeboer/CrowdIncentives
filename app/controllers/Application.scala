package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  def index = Action {
    implicit request =>
      if (session.get("user").isEmpty)
        Redirect("/login")
      else
        Ok(views.html.index(session.get("user").get))
  }
}
package controllers

import play.api._
import play.api.mvc._
import controllers.dal.TemplateDAL
import java.text.SimpleDateFormat
import java.util.Date
import controllers.utils.Config
import utils.U

object Application extends Controller {
  def index = Action {
    implicit request =>
      if (session.get("user").isEmpty)
        Redirect("/login")
      else {
        Redirect("/global")
      }
  }
}

case class User(id:Long, name: String, round:Long)
case class TemplatePart (id:Long, name:String, beforeText:String="", afterText:String="")
case class IntegratedStory(id:Long, name:String, createDate:Date, lastModification:Date) {
  def modificationDateFormatted = Config.sdf.format(createDate)
}
case class Part(id:Long, name:String, content:String="")
case class IndexData(user:User) {
  val templateParts = new TemplateDAL(user.id).getParts()
}

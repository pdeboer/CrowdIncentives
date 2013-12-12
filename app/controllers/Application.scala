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

//need to define all model classes here, otherwise invisible to views. that's ugly

case class User(id:Long, name: String, round:Long = -1L)
case class TemplatePart (id:Long, name:String, beforeText:String="", afterText:String="")
case class IntegratedStory(id:Long, name:String, createDate:Date, lastModification:Date, author:User = null, var parts:List[StoryPart] = null) {
  def modificationDateFormatted = Config.sdf.format(lastModification)

  def partForTemplate(templateId:Long):StoryPart = {
    if(parts == null) null else parts.find(_.template.id == templateId).getOrElse(null)
  }
}
case class StoryPart(id:Long, name:String, content:String="", createDate:Date, lastModification:Date, author:User = null, template:TemplatePart=null) {
  def modificationDateFormatted = Config.sdf.format(lastModification)
}
object StoryPart{
  def empty = StoryPart(-1, "", "", new Date(), new Date(), null)
}

case class IndexData(user:User) {
  val templateParts = new TemplateDAL(user.id).getParts()
}

class Counter(var init: Int = 0) {
  def incrementAndGet() = {
    init += 1
    init
  }
}
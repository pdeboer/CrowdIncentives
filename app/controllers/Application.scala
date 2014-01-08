package controllers

import play.api.mvc._
import controllers.dal.{RoundDAL, StoryDAL, TemplateDAL}
import java.util.Date
import controllers.utils.{U, Config}
import scala.collection.mutable
import java.text.SimpleDateFormat
import play.api.libs.json.{JsObject, JsValue, JsString, JsNumber}
import scala.util.parsing.json.JSON

object Application extends Controller {
  def index = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else if (session.get("user").isEmpty)
        Redirect("/login")
      else {
        val u=U.user(session)
        Ok(views.html.home(new RoundDAL().getRound(u.round).home,IndexData(u)))
      }
  }
}

//need to define all model classes here, otherwise invisible to views. that's ugly

case class User(id: Long, name: String, round: Long = -1L, isAdmin: Boolean = false, code:String="") {
  def toMap = JsObject(Seq("id"->JsNumber(id), "name"->JsString(name)))
}

case class TemplatePart(id: Long, name: String, beforeText: String = "", afterText: String = "")

case class FromTo(from: Date, to: Date) {
  def fromFormatted = Config.sdf.format(from)

  def toFormatted = Config.sdf.format(to)

  def isNow = new Date().compareTo(from) >= 0 && new Date().compareTo(to) <= 0

  def minutesLeft = (from.getTime - new Date().getTime) / 60000
}

case class IntegratedStory(id: Long, name: String, createDate: Date, lastModification: Date, author: User = null, var parts: List[StoryPart] = null) {
  def modificationDateFormatted = Config.sdf.format(lastModification)
  def creationDateFormatted = Config.sdf.format(createDate)

  private var _partForTemplateCache = new mutable.HashMap[Long, StoryPart]()

  def partForTemplate(templateId: Long): StoryPart = {
    if (parts == null) null
    else {
      if (!_partForTemplateCache.contains(templateId)) {
        _partForTemplateCache += templateId -> parts.find(_.template.id == templateId).getOrElse(null)
      }
      _partForTemplateCache(templateId)
    }
  }

  private var _candidatesCache = new collection.mutable.HashMap[Long, List[StoryPart]]()

  def candidatesForTemplatePart(partId: Long, user: User) = {
    if (!_candidatesCache.contains(partId)) {
      _candidatesCache += partId -> new StoryDAL(user.round).getParts(partId)
    }
    _candidatesCache(partId)
  }
}

object IntegratedStory {
  def empty = {
    IntegratedStory(-1, "", new Date(), new Date())
  }
}

case class StoryPart(id: Long, name: String, content: String = "", createDate: Date, lastModification: Date, author: User = null, template: TemplatePart = null, doubleValue:Double = 0d) {
  def modificationDateFormatted = Config.sdf.format(lastModification)
}

object StoryPart {
  def empty = StoryPart(-1, "", "", new Date(), new Date(), null)
}

case class IndexData(user: User) {
  val templateParts = new TemplateDAL(user.round).getParts()
}

class Counter(var init: Int = 0) {
  def incrementAndGet() = {
    init += 1
    init
  }

  def get() = init
}

case class Template(id:Long, name:String="", doubleValuesSummed:Boolean=false, multiPartSelection:Boolean=false, doubleValueName:String=null)

case class Setting(key: String, value: String)

object Settings extends Enumeration {
  type Settings = Value
  val ADMINPW = Value
}

case class Round(var id:Long = -1L, startTime:Date = new Date(), endTime:Date = new Date(), templateId:Long = 2, description:String="", notes:String="", home:String="") {
  def startTimeFormatted = Config.sdf.format(startTime)
  def endTimeFormatted = Config.sdf.format(endTime)
}

case class Code(code: String, round: Long)

case class Message(id: Long = -1, from: User, to: User = null, createDate: Date = new Date(), body:String) {
  def toMap =  {
    JsObject(Seq("id"->JsNumber(id), "from"->from.toMap, "to"->(if(to==null) JsNumber(-1) else to.toMap),
      "createDate"->JsString(Config.sdf.format(createDate)), "body"->JsString(body)))
  }
}
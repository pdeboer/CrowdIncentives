package controllers

import play.api._
import play.api.mvc._
import controllers.dal.{StoryDAL, TemplateDAL}
import java.text.SimpleDateFormat
import java.util.Date
import controllers.utils.Config
import utils.U
import scala.collection.mutable

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

case class User(id: Long, name: String, round: Long = -1L)

case class TemplatePart(id: Long, name: String, beforeText: String = "", afterText: String = "")

case class IntegratedStory(id: Long, name: String, createDate: Date, lastModification: Date, author: User = null, var parts: List[StoryPart] = null) {
  def modificationDateFormatted = Config.sdf.format(lastModification)

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

case class StoryPart(id: Long, name: String, content: String = "", createDate: Date, lastModification: Date, author: User = null, template: TemplatePart = null) {
  def modificationDateFormatted = Config.sdf.format(lastModification)
}

object StoryPart {
  def empty = StoryPart(-1, "", "", new Date(), new Date(), null)
}

case class IndexData(user: User) {
  val templateParts = new TemplateDAL(user.id).getParts()
}

class Counter(var init: Int = 0) {
  def incrementAndGet() = {
    init += 1
    init
  }

  def get() = init
}
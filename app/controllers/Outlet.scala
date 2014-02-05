package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{RoundDAL, TemplateDAL, StoryDAL}
import java.util.Date

/**
 * @author pdeboer
 */
object Outlet extends Controller {
  def show(roundId: Long, globalId: Long) = Action {
    implicit request =>
        val storyDAL = new StoryDAL(roundId)
        val global = storyDAL.getIntegratedStory(globalId)
        val templateDAL = new TemplateDAL(roundId)
        val template = templateDAL.getTemplate(new RoundDAL().getRound(roundId).templateId)

        Ok(views.html.integratedstory_outlet(global, templateDAL.getParts(),
          template))
  }
}

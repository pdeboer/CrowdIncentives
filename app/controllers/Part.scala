package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.U
import controllers.dal.{TemplateDAL, StoryDAL}

/**
 * @author pdeboer
 *         First created on 11/12/13 at 15:09
 */
object Part extends Controller {
  def list(templatePartId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      Ok(views.html.part(storyDAL.getParts(templatePartId),
        new TemplateDAL(u.round).getPart(templatePartId),
        IndexData(u)))
  }
}

package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.U
import controllers.dal.StoryDAL

/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object Global extends Controller {
  def list() = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      Ok(views.html.global(storyDAL.getIntegratedStories(),
        IndexData(u)))
  }
}

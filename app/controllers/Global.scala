package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{TemplateDAL, StoryDAL}
import java.util.Date

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


  def save(globalId: Long) = Action {
    implicit request =>
    //dal init
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val templateDAL = new TemplateDAL(u.round)

      //form handling
      val map = request.body.asFormUrlEncoded.get
      val name = map.get("name").get.head

      val associations = templateDAL.getParts().map(p => {
        val idOp = map.get("part" + p.id).getOrElse(List()).headOption
        val id = idOp.getOrElse(null)
        if (id == null) null else storyDAL.getPart(id.toLong)
      }).filterNot(_ == null)

      val global = IntegratedStory(globalId, name, new Date(), new Date(), u, associations)

      if (globalId > 0) {
        //update
        if (!Security.checkUserAllowedToEditGlobal(u, globalId)) {
          Forbidden(views.html.error(IndexData(u)))
        } else {
          storyDAL.updateIntegratedStory(global)
          Redirect("/global/edit/" + globalId)
        }
      } else {
        //insert
        val newId: Long = storyDAL.insertIntegratedStory(global)
        Redirect("/global/edit/" + newId)
      }

  }

  def create() = Action {
    implicit request =>
      val u = U.user(session)
      val templateDAL = new TemplateDAL(u.round)

      Ok(views.html.global_edit(IntegratedStory.empty, templateDAL.getParts(),
        IndexData(u)))
  }

  def edit(globalId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val templateDAL = new TemplateDAL(u.round)

      if (!Security.checkUserAllowedToEditGlobal(u, globalId)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        Ok(views.html.global_edit(storyDAL.getIntegratedStory(globalId), templateDAL.getParts(), IndexData(u)))
      }
  }

  def show(globalId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val global = storyDAL.getIntegratedStory(globalId)
      val templateDAL = new TemplateDAL(u.round)

      Ok(views.html.global_show(global, templateDAL.getParts(),
        IndexData(u)))
  }
}

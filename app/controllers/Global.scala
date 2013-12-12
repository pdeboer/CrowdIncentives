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
        storyDAL.getTemplateId(),
        IndexData(u)))
  }


  def save(templatePartId: Long, partId: Long) = Action {
    implicit request =>
    //dal init
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val templateDAL = new TemplateDAL(u.round)

      if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId))) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        //form handling
        val map = request.body.asFormUrlEncoded.get
        val name = map.get("name").get.head
        val content = map.get("content").get.head
        val part = StoryPart(partId, name, content, new Date(), new Date(), u)

        if (partId > 0) {
          //update
          if (Security.checkUserAllowedToEditPart(u, partId)) {
            Forbidden(views.html.error(IndexData(u)))
          } else {
            val current = storyDAL.getPart(partId)
            storyDAL.updatePart(partId, part)
            Redirect("/part/" + templatePartId + "/show/" + partId)
          }
        } else {
          //insert
          val newId: Option[Long] = storyDAL.insertPart(templatePartId, part)
          Redirect("/part/" + templatePartId + "/show/" + newId.get)
        }
      }
  }

  def create(templateId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val templateDAL = new TemplateDAL(u.round)

        Ok(views.html.global_edit(IntegratedStory.empty, templateDAL.getParts(), templateId,
          IndexData(u)))
  }

  def edit(templatePartId: Long, partId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val part = storyDAL.getPart(partId)
      val templateDAL = new TemplateDAL(u.round)

      if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId)) ||
        !Security.checkUserAllowedToEditPart(u, partId))
        Forbidden(views.html.error(IndexData(u)))
      else
        Ok(views.html.part_edit(part, templateDAL.getPart(templatePartId),
          IndexData(u)))
  }

  def show(templatePartId: Long, globalId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val global = storyDAL.getIntegratedStory(globalId)
      val templateDAL = new TemplateDAL(u.round)

      Ok(views.html.global_show(global, templateDAL.getParts(),
        IndexData(u)))
  }
}

package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{TemplateDAL, StoryDAL}
import java.util.Date

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
          val current = storyDAL.getPart(partId)
          if (current.author.id != u.id) {
            Forbidden(views.html.error(IndexData(u)))
          } else {
            storyDAL.updatePart(partId, part)
            Redirect("/part/" + templatePartId + "/show/" + partId)
          }
        } else {
          //insert
          val newId:Option[Long] = storyDAL.insertPart(templatePartId, part)
          Redirect("/part/" + templatePartId + "/show/" + newId.get)
        }
      }
  }

  def create(templatePartId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val templateDAL = new TemplateDAL(u.round)
      Ok(views.html.part_edit(StoryPart.empty, templateDAL.getPart(templatePartId),
        IndexData(u)))
  }

  def edit(templatePartId: Long, partId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val part = storyDAL.getPart(partId)
      val templateDAL = new TemplateDAL(u.round)
      Ok(views.html.part_edit(part, templateDAL.getPart(templatePartId),
        IndexData(u)))
  }

  def show(templatePartId: Long, partId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      val storyDAL = new StoryDAL(u.round)
      val part = storyDAL.getPart(partId)
      val templateDAL = new TemplateDAL(u.round)
      Ok(views.html.part_show(part, templateDAL.getPart(templatePartId),
        IndexData(u)))
  }
}

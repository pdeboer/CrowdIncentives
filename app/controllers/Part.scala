package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{RoundDAL, TemplateDAL, StoryDAL}
import java.util.Date

/**
 * @author pdeboer
 *         First created on 11/12/13 at 15:09
 */
object Part extends Controller {
  def list(templatePartId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val templateDAL: TemplateDAL = new TemplateDAL(u.round)

        if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId)))
          Forbidden(views.html.error(IndexData(u)))
        else {
          val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)
          Ok(views.html.part(storyDAL.getParts(templatePartId),
            templateDAL.getPart(templatePartId),
            template,
            IndexData(u)))

        }
      }
  }

  def save(templatePartId: Long, partId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        //dal init
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val templateDAL = new TemplateDAL(u.round)

        if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId))) {
          Forbidden(views.html.error(IndexData(u)))
        } else {
          //form handling
          val map = request.body.asMultipartFormData.get.asFormUrlEncoded
          val name = map.get("name").get.head
          val content = map.get("content").get.head
          val doubleValueOption = map.get("doubleValue").getOrElse(Nil).headOption
          val doubleValueNumeric = doubleValueOption.getOrElse("0").replaceAll("[^0-9\\.]", "")
          val url = map.get("url").getOrElse(Nil).headOption.getOrElse(null)

          val imageName = request.body.asMultipartFormData.get.file("image")
          val image = if (!imageName.isEmpty) {
            val file = imageName.get
            val ctype = file.contentType.getOrElse("")
            val data = scalax.io.Resource.fromFile(file.ref.file).byteArray

            Some(data)
          } else None

          val part = StoryPart(partId, name, content, new Date(), new Date(), u,
            doubleValue = doubleValueNumeric.toDouble,
            url = url)

          if (partId > 0) {
            //update
            if (!Security.checkUserAllowedToEditPart(u, partId)) {
              Forbidden(views.html.error(IndexData(u)))
            } else {
              storyDAL.updatePart(partId, part, image)
              Redirect("/part/" + templatePartId + "/show/" + partId)
            }
          } else {
            //insert
            val newId: Option[Long] = storyDAL.insertPart(templatePartId, part, image)
            Redirect("/part/" + templatePartId + "/show/" + newId.get)
          }
        }
      }
  }

  def create(templatePartId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val templateDAL = new TemplateDAL(u.round)

        if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId)))
          Forbidden(views.html.error(IndexData(u)))
        else {
          val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)
          Ok(views.html.part_edit(StoryPart.empty, templateDAL.getPart(templatePartId),
            template, IndexData(u)))
        }
      }
  }

  def edit(templatePartId: Long, partId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val part = storyDAL.getPart(partId)
        val templateDAL = new TemplateDAL(u.round)

        if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId)) ||
          !Security.checkUserAllowedToEditPart(u, partId))
          Forbidden(views.html.error(IndexData(u)))
        else {
          val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)
          Ok(views.html.part_edit(part, templateDAL.getPart(templatePartId), template,
            IndexData(u)))
        }
      }
  }

  def show(templatePartId: Long, partId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val part = storyDAL.getPart(partId)
        val templateDAL = new TemplateDAL(u.round)

        val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)

        if (!Security.checkUserAccessToTemplatePart(u, templateDAL.getPart(templatePartId)))
          Forbidden(views.html.error(IndexData(u)))
        else
          Ok(views.html.part_show(part, templateDAL.getPart(templatePartId), template,
            IndexData(u)))
      }
  }
}

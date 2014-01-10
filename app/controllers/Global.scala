package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{RoundDAL, TemplateDAL, StoryDAL}
import java.util.Date

/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object Global extends Controller {
  def list() = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val template = new TemplateDAL(u.round).getTemplate(new RoundDAL().getRound(u.round).templateId)
        Ok(views.html.global(storyDAL.getIntegratedStories(), template,
          IndexData(u)))
      }
  }


  def save(globalId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        //dal init
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val templateDAL = new TemplateDAL(u.round)

        //form handling
        val map = request.body.asFormUrlEncoded.get
        val name = map.get("name").get.head

        val associations = templateDAL.getParts().map(p => {
          val idOp = map.get("part" + p.id).getOrElse(Nil)
          if (idOp.isEmpty) null else idOp.map(p => storyDAL.getPart(p.toLong))
        }).filterNot(_ == null).flatten

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
  }

  def create() = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val templateDAL = new TemplateDAL(u.round)
        val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)

        Ok(views.html.global_edit(IntegratedStory.empty(u), templateDAL.getParts(),
          template, IndexData(u)))
      }
  }

  def edit(globalId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val templateDAL = new TemplateDAL(u.round)

        if (!Security.checkUserAllowedToEditGlobal(u, globalId)) {
          Forbidden(views.html.error(IndexData(u)))
        } else {
          val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)
          Ok(views.html.global_edit(storyDAL.getIntegratedStory(globalId), templateDAL.getParts(), template, IndexData(u)))
        }
      }
  }

  def show(globalId: Long) = Action {
    implicit request =>
      if (utils.Security.checkIfRedirectToWaitingRoom(U.user(session))) {
        Redirect("/wait")
      } else {
        val u = U.user(session)
        val storyDAL = new StoryDAL(u.round)
        val global = storyDAL.getIntegratedStory(globalId)
        val templateDAL = new TemplateDAL(u.round)
        val template = templateDAL.getTemplate(new RoundDAL().getRound(u.round).templateId)

        Ok(views.html.global_show(global, templateDAL.getParts(),
          template, IndexData(u)))
      }
  }
}

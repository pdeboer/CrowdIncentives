package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{RoundDAL, TemplateDAL, StoryDAL}
import java.util.Date

/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object Admin extends Controller {
  def listRounds() = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        val roundDAL = new RoundDAL()
        Ok(views.html.admin(roundDAL.getRounds(),
          IndexData(u)))
      }
  }

  def edit(roundId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        val roundDAL = new RoundDAL()
        Ok(views.html.round_edit(roundDAL.getRound(roundId), IndexData(u)))
      }
  }


  def create() = Action {
    implicit request =>
      implicit request =>
        val u = U.user(session)
        if (!utils.Security.checkIsAdmin(u)) {
          Forbidden(views.html.error(IndexData(u)))
        } else {
          Ok(views.html.round_edit(Round(), IndexData(u)))
        }
  }

  def save(roundId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        //dal init
        val roundDAL = new RoundDAL()

        //form handling
        val map = request.body.asFormUrlEncoded.get
        val description = map.get("description").get.head
        val startTime = map.get("startTime").get.head
        val endTime = map.get("endTime").get.head
        val templateId = map.get("templateId").get.head
        val notes = map.get("notes").get.head

        val startTimeParsed = Date.parse(startTime)
        val endTimeParsed = Date.parse(endTime)
        val templateIdParsed = templateId.toLong

        val newRound = Round(roundId, startTimeParsed, endTimeParsed, templateIdParsed, description, notes)

        if (roundId > 0) {
          storyDAL.updateIntegratedStory(global)
          Redirect("/global/edit/" + roundId)
        } else {
          //insert
          val newId: Long = storyDAL.insertIntegratedStory(global)
          Redirect("/global/edit/" + newId)
        }
      }
  }

}

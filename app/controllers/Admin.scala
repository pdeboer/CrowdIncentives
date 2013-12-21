package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{UserDAL, RoundDAL, TemplateDAL, StoryDAL}
import java.util.Date

/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object Admin extends Controller {
  def index() = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        val roundDAL = new RoundDAL()
        Ok(views.html.admin(roundDAL.getRounds(), roundDAL.getRemainingCodes(),
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
        Ok(views.html.round_edit(roundDAL.getRound(roundId), false, IndexData(u)))
      }
  }


  def create() = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        Ok(views.html.round_edit(Round(), isInsert=true, IndexData(u)))
      }
  }

  def changeRoundOfCurrentUser() = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        val map = request.body.asFormUrlEncoded.get
        val newRound = map.get("newRound").get.head

        val userDAL = new UserDAL()
        userDAL.setUserRound(u.id, newRound.toLong)

        Redirect("/admin")
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

        val startTimeParsed = Round().sdf.parse(startTime)
        val endTimeParsed = Round().sdf.parse(endTime)
        val templateIdParsed = templateId.toLong

        val newRound = Round(roundId, startTimeParsed, endTimeParsed, templateIdParsed, description, notes)

        if (roundId > 0) {
          roundDAL.updateRound(newRound)
          Redirect("/admin/edit/" + roundId)
        } else {
          val prevRoundId = map.get("prevround").get.head
          //insert
          val newId: Long = roundDAL.insertRound(newRound, prevRoundId)
          Redirect("/admin/edit/" + newId)
        }
      }
  }

}

package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Config, Security, U}
import controllers.dal._
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
        val pings = new PingDAL(roundId).getAllPings()
        val onlineUsers = roundDAL.getUsers(roundId)
        val globals = new StoryDAL(roundId).getIntegratedStories()

        Ok(views.html.round_edit(roundDAL.getRound(roundId), onlineUsers, pings, globals, IndexData(u), isInsert =  false))
      }
  }

  def copyPart() = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        val map = request.body.asFormUrlEncoded.get
        val sourcePart = map.get("part").get.head
        val targetRound = map.get("target").get.head

        val storyDAL = new StoryDAL(u.round)
        storyDAL.copyPartToRound(sourcePart.toLong, targetRound.toLong)

        Redirect("/part/"+storyDAL.getPart(sourcePart.toLong, fetchTemplatePart = true).template.id)
      }
  }

  def create() = Action {
    implicit request =>
      val u = U.user(session)
      if (!utils.Security.checkIsAdmin(u)) {
        Forbidden(views.html.error(IndexData(u)))
      } else {
        Ok(views.html.round_edit(Round(), Nil, Nil, Nil, IndexData(u), isInsert=true))
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
        val hometext = map.get("hometext").get.head

        val startTimeParsed = Config.sdf.parse(startTime)
        val endTimeParsed = Config.sdf.parse(endTime)
        val templateIdParsed = templateId.toLong

        val newRound = Round(roundId, startTimeParsed, endTimeParsed, templateIdParsed, description, notes, home = hometext)

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

package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal._
import java.util.Date
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.libs.json.JsArray
import controllers.Message
import play.api.libs.json.JsObject


/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object PingController extends Controller {
  def getOnline() = Action {
    implicit request =>
      val u = U.user(session)
      if (utils.Security.checkIfRedirectToWaitingRoom(u)) {
        Redirect("/wait")
      } else {
        //also set user active
        val pingDAL = new PingDAL(u.round)
        pingDAL.setActive(u.id)

        val onlineUsers = pingDAL.getOnlineUsers(60) //one minute

        val res = JsObject(
          Seq("online" -> JsArray(onlineUsers.map(_.toJson)))
        )
        val resString = res.toString()

        Ok(views.html.plain(resString.substring(1, resString.size - 1)))
      }
  }
}

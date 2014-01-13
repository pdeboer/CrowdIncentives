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
object MessageController extends Controller {
  def allMessages() = Action {
    implicit request =>
      val u = U.user(session)
      if (utils.Security.checkIfRedirectToWaitingRoom(u)) {
        Redirect("/wait")
      } else {
        //also set user active
        val pingDAL = new PingDAL(u.round)
        pingDAL.setActive(u.id)

        val messageDAL = new MessageDAL(u.round)
        val res = JsObject(
          Seq("messages" -> JsArray(messageDAL.getAllMessages().map(_.toMap)))
        )
        val resString = res.toString()

        Ok(views.html.plain(resString.substring(1, resString.size - 1)))
      }
  }

  def sendPublicMessage() = Action {
    implicit request =>
      val u = U.user(session)
      if (utils.Security.checkIfRedirectToWaitingRoom(u)) {
        Redirect("/wait")
      } else {
        val map = request.body.asFormUrlEncoded.get
        val messageBody = map.get("message").get.head
        val message = Message(from = u, body = messageBody)

        val messageDAL = new MessageDAL(u.round)
        messageDAL.sendMessage(message)

        Ok(views.html.plain("200"))
      }
  }
}

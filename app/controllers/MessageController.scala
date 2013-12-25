package controllers

import play.api.mvc.{Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal.{UserDAL, RoundDAL, TemplateDAL, StoryDAL, MessageDAL}
import java.util.Date
import play.api.libs.json.{JsArray, JsObject, Json}


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
        val messageDAL = new MessageDAL(u.round)
        val res: JsObject = JsObject(
          Seq("messages" -> JsArray(messageDAL.getAllMessages().map(_.toMap)))
        )
        Ok(views.html.plain(res.toString()))
      }
  }

}

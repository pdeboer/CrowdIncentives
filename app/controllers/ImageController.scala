package controllers

import play.api.mvc.{ResponseHeader, SimpleResult, Action, Controller}
import controllers.utils.{Security, U}
import controllers.dal._
import java.util.Date
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.iteratee.Enumerator
import java.io.ByteArrayInputStream


/**
 * @author pdeboer
 *         First created on 10/12/13 at 11:52
 */
object ImageController extends Controller {
  def get(imageId: Long) = Action {
    implicit request =>
      val u = U.user(session)
      if (utils.Security.checkIfRedirectToWaitingRoom(u)) {
        Redirect("/wait")
      } else {
        val data = new ImageDAL().get(imageId)
        if (data.isEmpty) NotFound
        else {
          SimpleResult(
            header = ResponseHeader(200, Map(CONTENT_LENGTH -> data.get.size.toString)),
            body = Enumerator.fromStream(new ByteArrayInputStream(data.get))
          )
        }
      }
  }
}

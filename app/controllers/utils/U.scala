package controllers.utils

import play.api.mvc.Session
import controllers.dal.UserDAL
import controllers.User

/**
 * @author pdeboer
 *         First created on 10/12/13 at 18:12
 */
object U {
  def user(s:Session):User = new UserDAL().getUser(s.get("user").getOrElse("-1").toInt)
}

package dal

import anorm._
import play.api.db.DB
import play.api.Play.current
import controllers.UserData

/**
 * @author pdeboer
 *         First created on 09/12/13 at 17:54
 */
class LoginDAL {


  def checkLogin(u:UserData): Boolean = {
    if(u.name==null||u.password==null) false
    else DB.withConnection {
      implicit c =>
        val check = SQL("SELECT id FROM users WHERE username = {username} AND password={pw}").on('username -> u.name, 'pw -> u.password)()

        check.size == 1
    }
  }

  def register(u:UserData): Boolean = {
    DB.withConnection {
      implicit c =>
        val userExists = SQL("SELECT id FROM users WHERE username = {username}").on('username -> u.name)().size == 1

        if (userExists) false
        else {
          val i: Option[Long] = SQL("INSERT INTO users (username, password) VALUES({username}, {pw})")
            .on('username -> u.name, 'pw -> u.password)
            .executeInsert()
          i.isEmpty
        }
    }
  }
}

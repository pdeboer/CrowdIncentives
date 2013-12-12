package controllers.dal

import anorm._
import play.api.db.DB
import controllers.User
import play.api.Play.current

/**
 * @author pdeboer
 *         First created on 09/12/13 at 17:54
 */
class UserDAL {
  def checkLogin(name:String, password:String): Long = {
    if (name == null || password == null) -1
    else DB.withConnection {
      implicit c =>
        val check = SQL("SELECT id FROM users WHERE username = {username} AND password={pw}").on('username -> name, 'pw -> password)().headOption

        if(check.isEmpty) -1
        else check.get.apply[Long]("id")
    }
  }

  def register(name:String, password:String, code:String): Boolean = {
    DB.withConnection {
      implicit c =>
        val userExists = SQL("SELECT id FROM users WHERE username = {username}").on('username -> name)().size == 1

        val codeExistsAndUnused = SQL(
          """
            SELECT round_id FROM codes
            WHERE code={code} AND code NOT IN (
              SELECT code FROM users
            )
          """).on('code->code)()

        if (userExists || codeExistsAndUnused.headOption.isEmpty) false
        else {
          val round = codeExistsAndUnused.head.apply[Long]("round_id")

          val i: Option[Long] = SQL("INSERT INTO users (username, password, round) VALUES({username}, {pw}, {round})")
            .on('username -> name, 'pw -> password, 'round->round)
            .executeInsert()
          i.isEmpty
        }
    }
  }

  def getUser(id:Int):User = {
    DB.withConnection {
      implicit c=>
        val u = SQL("SELECT id, username, round FROM users WHERE id={id}").on('id->id)().headOption

        if(u.isEmpty) null else User(u.get.apply[Long]("id"), u.get.apply[String]("username"), u.get.apply[Long]("round"))
    }
  }
}

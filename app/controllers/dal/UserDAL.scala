package controllers.dal

import anorm._
import play.api.db.DB
import controllers.{FromTo, User}
import play.api.Play.current
import java.util.Date

/**
 * @author pdeboer
 *         First created on 09/12/13 at 17:54
 */
class UserDAL {
  def checkLogin(name: String, password: String): Long = {
    if (name == null || password == null) -1
    else DB.withConnection {
      implicit c =>
        val check = SQL("SELECT id FROM users WHERE username = {username} AND password={pw}").on('username -> name, 'pw -> password)().headOption

        if (check.isEmpty) -1
        else check.get.apply[Long]("id")
    }
  }

  def register(name: String, password: String, code: String): Boolean = {
    if (name.length < 5 || password.length < 8) false
    else {
      DB.withConnection {
        implicit c =>
          val userExists = SQL("SELECT id FROM users WHERE username = {username}").on('username -> name)().size == 1

          if (userExists) {
            false
          } else {
            val round = new RoundDAL().getNextRound()

            val i: Option[Long] = SQL("INSERT INTO users (username, password, round, code) VALUES({username}, {pw}, {round}, {code})")
              .on('username -> name, 'pw -> password, 'round -> round.id, 'code -> code)
              .executeInsert()
            !i.isEmpty
          }
      }
    }
  }

  def setUserRound(userId:Long, roundId:Long) {
    DB.withConnection {
      implicit c=>
        SQL("UPDATE users SET round={round} WHERE id={id}").on('round->roundId, 'id->userId).executeUpdate()
    }
  }

  def getUser(id: Int): User = {
    DB.withConnection {
      implicit c =>
        val u = SQL("SELECT id, username, round, isAdmin FROM users WHERE id={id}").on('id -> id)().headOption

        if (u.isEmpty) null else User(u.get.apply[Long]("id"), u.get.apply[String]("username"), u.get.apply[Long]("round"), u.get.apply[Boolean]("isAdmin") )
    }
  }

  def userRoundTimeframe(usr: User) = {
    DB.withConnection {
      implicit c =>
        val userId = if (usr == null) -1L else usr.id

        val query: String = if (userId >= 0)
          "SELECT fromTime, toTime FROM users u INNER JOIN round r ON u.round = r.id WHERE u.id={id}"
        else "SELECT MAX(fromTime) as fromTime, MAX(toTime) as toTime FROM round"

        val u = SQL(query).on('id -> userId)().headOption

        if (u.isEmpty) null else FromTo(u.get.apply[Date]("fromTime"), u.get.apply[Date]("toTime"))
    }
  }
}

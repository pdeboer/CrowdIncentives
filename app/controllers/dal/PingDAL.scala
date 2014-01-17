package controllers.dal

import play.api.db.DB
import anorm._
import play.api.Play.current
import controllers._
import java.util.Date


/**
 * @author pdeboer
 *         First created on 10/12/13 at 18:02
 */
class PingDAL(val round: Long) {
  def setActive(userId: Long) {
    DB.withConnection {
      implicit c =>
        SQL("INSERT INTO ping (user, round, ping_time) VALUES ({user}, {round}, {ptime})")
          .on('user -> userId, 'round -> round, 'ptime -> new Date()).executeInsert()
    }
  }

  def getAllPings() = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT p.ping_time, u.id, u.username
            FROM ping p INNER JOIN users u ON p.user = u.id
            WHERE p.round = {round}
            ORDER BY p.ping_time DESC
          """
        ).on('round -> round)().map(r => extractPing(r))

        data.toList
    }
  }

  private def extractPing(r: SqlRow): Ping = {
    Ping(
      user = User(id = r[Long]("id"), name = r[String]("username")),
      round = round, time = r[Date]("ping_time"))
  }

  def getOnlineUsers(lastActivitySeconds: Long) = {
    DB.withConnection {
      implicit c =>
        val minDate = new Date(new Date().getTime - lastActivitySeconds * 1000)
        val data = SQL( """
            SELECT p.ping_time, u.id, u.username
            FROM ping p INNER JOIN users u ON p.user = u.id
            WHERE p.round = {round} and p.ping_time > {minDate}
            GROUP BY u.id
            ORDER BY p.ping_time DESC
                        """).on('round -> round, 'minDate -> minDate)().map(r => extractPing(r))
        data.toList
    }
  }
}

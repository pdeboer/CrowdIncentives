package controllers.dal

import play.api.db.DB
import anorm._
import play.api.Play.current
import controllers._
import java.util.Date
import controllers.IntegratedStory
import controllers.User
import controllers.StoryPart


/**
 * @author pdeboer
 *         First created on 10/12/13 at 18:02
 */
class MessageDAL(val roundId: Long) {
  private val userDAL = new UserDAL()

  def getAllMessages() = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT m.id, m.user_from, f.username as from_name, m.user_to, '' AS to_name, m.create_date, m.body
            FROM messages m INNER JOIN users f ON m.user_from = f.id
            WHERE m.round_id = {round} AND m.user_to IS NULL
            ORDER BY m.create_date DESC
            LIMIT 200
          """
        ).on('round -> roundId)().map(r => extractMessage(r))

        data.toList
    }
  }

  def getAllMessagesFor(user: Long) = {
    DB.withConnection {
      implicit c =>
        val r = SQL(
          """
            SELECT m.id, m.user_from, f.name as from_name, m.user_to, t.name as to_name, m.create_date,
              m.body
            FROM messages m INNER JOIN users f ON m.user_from = f.id
              LEFT JOIN users t ON m.user_to = t.id
            WHERE m.round_id = {round} AND m.user_to = {to}
            ORDER BY m.create_date DESC
          """
        ).on('round -> roundId, 'to -> user)().headOption

        if (r.isEmpty) null else extractMessage(r.get)
    }
  }

  private def extractMessage(row: SqlRow): Message = {
    val m = Message(
      id = row[Long]("id"), from = userDAL.getUser(row[Long]("user_from").toInt),
      to = if (row[Option[Long]]("user_to").isEmpty) null
          else userDAL.getUser(row[Option[Long]]("user_to").get.toInt),
      createDate = row[Date]("create_date"), body = row[String]("body")
    )
    m
  }

  def sendMessage(m:Message) = {
    DB.withConnection {
      implicit c =>
        val id = SQL(
          """
            INSERT INTO messages
              (user_from,user_to, body, round_id, create_date)
            VALUES ({userfrom}, {userto}, {body}, {round}, {createdate})
          """.replaceAll("\\{userto\\}", if(m.to == null) "NULL" else m.to.id +"")
        ).on('userfrom->m.from.id,
          'body->m.body, 'round->roundId, 'createdate->m.createDate)
          .executeInsert()

        id
    }
  }
}

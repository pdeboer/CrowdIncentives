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
  def getAllMessages() = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT m.id, m.user_from, f.username as from_name, m.user_to, '' AS to_name, m.create_date, m.body
            FROM messages m INNER JOIN users f ON m.user_from = f.id
            WHERE m.round_id = {round} AND m.user_to IS NULL
            ORDER BY m.create_date DESC
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
            SELECT m.id, m.user_from, f.name, m.user_to, t.name, m.create_date,
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
    Message(
      id = row[Long]("id"), from = User(row[Long]("messages.user_from"), row.metaData.getAliased("from_name").get._3),
      to = if (row[Option[Long]]("user_to").isEmpty) null
          else User(row[Option[Long]]("user_to").get, row.metaData.getAliased("to_name").get._3),
      createDate = row[Date]("create_date"), body = row[String]("body")
    )
  }

  def updatePart(partId: Long, updated: StoryPart) {
    DB.withConnection {
      implicit c =>
        SQL(
          """
            UPDATE part
            SET name = {name}, body = {body}, last_modification = NOW(), user_id = {author}
            WHERE id = {id}
          """).on('name -> updated.name, 'body -> updated.content,
            'author -> updated.author.id, 'id -> updated.id).executeUpdate()
    }
  }

  def insertPart(templatePartId: Long, newPart: StoryPart) = {
    DB.withConnection {
      implicit c =>
        val id = SQL(
          """
            INSERT INTO part
              (name, body, create_date, last_modification, user_id, template_part_id, round_id)
            VALUES ({name}, {body},{create}, {mod}, {author}, {templatePart}, {round})
          """
        ).on('name -> newPart.name, 'body -> newPart.content,
            'author -> newPart.author.id, 'templatePart -> templatePartId, 'round -> roundId, 'create -> newPart.createDate, 'mod -> newPart.lastModification)
          .executeInsert()

        id
    }
  }
}

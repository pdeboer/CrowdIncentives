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
class StoryDAL(val roundId: Long) {
  def getIntegratedStories(): List[IntegratedStory] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT g.id, g.name, g.create_date, g.last_modification, u.username, g.user_id
            FROM global g INNER JOIN users u ON g.user_id = u.id
            WHERE round_id = {round}
            ORDER BY last_modification DESC
          """
        ).on('round -> roundId)().map(r =>
          IntegratedStory(r[Long]("id"), r[String]("name"), r[Date]("create_date"), r[Date]("last_modification"), author = User(r[Long]("user_id"), r[String]("username")))
          )

        data.toList
    }
  }

  def getIntegratedStory(globalId: Long) = {
    DB.withConnection {
      implicit c =>
        val r = SQL(
          """
            SELECT g.id, g.name, p.create_date, p.last_modification, u.username, p.user_id
            FROM global g INNER JOIN users u ON g.user_id = u.id
            WHERE g.id={id} and g.round_id={round}
          """).on('id -> globalId, 'round -> roundId)().headOption

        val story = if (r.isEmpty) null else IntegratedStory(r.get.apply[Long]("id"), r.get.apply[String]("name"), r.get.apply[Date]("create_date"), r.get.apply[Date]("last_modification"), User(r.get.apply[Long]("user_id"), r.get.apply[String]("username")))

        val data = SQL(
          """
            SELECT p.id, p.name, p.body, p.create_date, p.last_modification, u.username, p.user_id,
              tp.id AS tpid, tp.description as tpdesc, tp.before_text as tpbefore, after_text as tpafter
            FROM part p INNER JOIN users u ON p.user_id = u.id
              INNER JOIN global_parts gp ON p.id = gp.part_id AND gp.global_id = {global}
              INNER JOIN template_part tp ON p.template_id = tp.id
            WHERE p.round_id = {round}
            ORDER BY last_modification DESC
          """
        ).on('round -> roundId, 'global -> globalId)().map(r =>
          StoryPart(r[Long]("id"), r[String]("name"), r[String]("body"), r[Date]("create_date"), r[Date]("last_modification"),
            author = User(r[Long]("user_id"), r[String]("username")),
            template = TemplatePart(r[Long]("tpd"), r[String]("tpdesc"), r[String]("tpbefore"), r[String]("tpafter")))
          )

        story.parts = data.toList

        story
    }
  }

  def getParts(templatePartId: Long) = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT p.id, p.name, p.body, p.create_date, p.last_modification, u.username, p.user_id
            FROM part p INNER JOIN users u ON p.user_id = u.id
            WHERE round_id = {round} AND template_part_id = {template}
            ORDER BY last_modification DESC
          """
        ).on('round -> roundId, 'template -> templatePartId)().map(r =>
          StoryPart(r[Long]("id"), r[String]("name"), r[String]("body"), r[Date]("create_date"), r[Date]("last_modification"), author = User(r[Long]("user_id"), r[String]("username")))
          )

        data.toList
    }
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
            VALUES ({name}, {body}, NOW(), NOW(), {author}, {templatePart}, {round})
          """
        ).on('name -> newPart.name, 'body -> newPart.content,
            'author -> newPart.author.id, 'templatePart -> templatePartId, 'round -> roundId)
          .executeInsert()

        id
    }
  }

  def getTemplateId(): Long = {
    DB.withConnection {
      implicit c =>
        val r = SQL(
          """
            SELECT r.template_id
            FROM round r INNER JOIN users u ON r.id = u.round
            WHERE u.round={round}
          """).on( 'round -> roundId)().headOption

        if (r.isEmpty) -1 else r.get.apply[Long]("template_id")
    }
  }

  def getPart(partId: Long): StoryPart = {
    DB.withConnection {
      implicit c =>
        val r = SQL(
          """
            SELECT p.id, p.name, p.body, p.create_date, p.last_modification, u.username, p.user_id
            FROM part p INNER JOIN users u ON p.user_id = u.id
            WHERE p.id={id} and p.round_id={round}
          """).on('id -> partId, 'round -> roundId)().headOption

        if (r.isEmpty) null else StoryPart(r.get.apply[Long]("id"), r.get.apply[String]("name"), r.get.apply[String]("body"), r.get.apply[Date]("create_date"), r.get.apply[Date]("last_modification"), author = User(r.get.apply[Long]("user_id"), r.get.apply[String]("username")))
    }
  }
}

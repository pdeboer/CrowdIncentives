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
            SELECT g.id, g.name, g.create_date, g.last_modification, u.username,
                  g.user_id, u.code
            FROM global g INNER JOIN users u ON g.user_id = u.id
            WHERE g.id={id} and g.round_id={round}
          """).on('id -> globalId, 'round -> roundId)().headOption

        val story = if (r.isEmpty) null else IntegratedStory(r.get.apply[Long]("id"), r.get.apply[String]("name"), r.get.apply[Date]("create_date"), r.get.apply[Date]("last_modification"), User(r.get.apply[Long]("user_id"), r.get.apply[String]("username"), code = r.get.apply[String]("code")))

        val data = SQL(
          """
            SELECT p.id, p.name, p.body, p.create_date, p.last_modification, u.username, p.user_id, tp.id AS templatepart_id, tp.description as tpdesc, tp.before_text as tpbefore, after_text as tpafter, u.code
            FROM part p INNER JOIN users u ON p.user_id = u.id
              INNER JOIN global_parts gp ON p.id = gp.part_id AND gp.global_id = {global}
              INNER JOIN template_part tp ON p.template_part_id = tp.id
            WHERE p.round_id = {round}
            ORDER BY tp.id ASC
          """
        ).on('round -> roundId, 'global -> globalId)().map(r =>
          StoryPart(r[Long]("part.id"), r[String]("part.name"), r[String]("part.body"), r[Date]("part.create_date"), r[Date]("part.last_modification"),
            author = User(r[Long]("part.user_id"), r[String]("users.username"), code = r[String]("code")),
            template = TemplatePart(r[Long]("template_part.id"), r[String]("template_part.description"), r[String]("template_part.before_text"), r[String]("template_part.after_text")))
          )

        story.parts = data.toList

        story
    }
  }

  def insertIntegratedStory(newStory: IntegratedStory) = {
    DB.withConnection {
      implicit c =>
        val id: Option[Long] = SQL(
          """
            INSERT INTO global (name, create_date, last_modification, user_id, round_id)
            VALUES ({name}, {create}, {mod}, {user}, {round})
          """
        ).on('name -> newStory.name, 'user -> newStory.author.id, 'round -> roundId,
            'create -> newStory.createDate, 'mod -> newStory.lastModification)
          .executeInsert()

        //save all associations
        updateIntegratedStory(IntegratedStory(id.get, newStory.name, newStory.createDate, newStory.lastModification, newStory.author, newStory.parts))

        id.get
    }
  }

  def updateIntegratedStory(newStory: IntegratedStory) {
    DB.withConnection {
      implicit c =>
      //update name
        SQL( """
          UPDATE global SET name={name}, last_modification=NOW()
          WHERE id={id}
             """).on('name -> newStory.name, 'id -> newStory.id).executeUpdate()


        //delete old
        SQL(
          """
          DELETE FROM global_parts WHERE global_id={id}
          """).on('id -> newStory.id).executeUpdate()

        newStory.parts.foreach(p => {
          SQL(
            """
              INSERT INTO global_parts (global_id, part_id)
              VALUES({global}, {part})
            """).on('global -> newStory.id, 'part -> p.id).executeInsert()
        })
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
            SET name = {name}, body = {body}, last_modification = NOW(),
                user_id = {author}, doubleValue = {doubleValue}
            WHERE id = {id}
          """).on('name -> updated.name, 'body -> updated.content,
            'author -> updated.author.id, 'id -> updated.id,
            'doubleValue -> updated.doubleValue).executeUpdate()
    }
  }

  def insertPart(templatePartId: Long, newPart: StoryPart) = {
    DB.withConnection {
      implicit c =>
        val id = SQL(
          """
            INSERT INTO part
              (name, body, create_date, last_modification, user_id, template_part_id, round_id, doubleValue)
            VALUES ({name}, {body},{create}, {mod}, {author}, {templatePart}, {round}, {doubleValue})
          """
        ).on('name -> newPart.name, 'body -> newPart.content,
            'author -> newPart.author.id, 'templatePart -> templatePartId, 'round -> roundId, 'create -> newPart.createDate, 'mod -> newPart.lastModification, 'doubleValue -> newPart.doubleValue)
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
          """).on('round -> roundId)().headOption

        if (r.isEmpty) -1 else r.get.apply[Long]("template_id")
    }
  }

  def getPart(partId: Long): StoryPart = {
    DB.withConnection {
      implicit c =>
        val r = SQL(
          """
            SELECT p.id, p.name, p.body, p.create_date, p.last_modification, u.username, p.user_id, u.code, p.doubleValue
            FROM part p INNER JOIN users u ON p.user_id = u.id
            WHERE p.id={id} and p.round_id={round}
          """).on('id -> partId, 'round -> roundId)().headOption

        if (r.isEmpty) null
        else StoryPart(r.get.apply[Long]("id"), r.get.apply[String]("name"), r.get.apply[String]("body"), r.get.apply[Date]("create_date"), r.get.apply[Date]("last_modification"), author = User(r.get.apply[Long]("user_id"), r.get.apply[String]("username"), code = r.get.apply[String]("code")),
          doubleValue = r.get.apply[Double]("doubleValue"))
    }
  }
}

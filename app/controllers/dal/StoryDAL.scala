package controllers.dal

import play.api.db.DB
import anorm._
import play.api.Play.current
import controllers.{Part, User, IntegratedStory, Global}
import java.util.Date


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

  def getParts(templatePartId:Long) = {
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
          Part(r[Long]("id"), r[String]("name"), r[String]("body"), r[Date]("create_date"), r[Date]("last_modification"), author = User(r[Long]("user_id"), r[String]("username")))
          )

        data.toList
    }
  }

}

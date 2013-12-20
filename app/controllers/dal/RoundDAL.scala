package controllers.dal

import play.api.db.DB
import controllers.TemplatePart
import anorm._
import play.api.Play.current

/**
 * @author pdeboer
 *         First created on 09/12/13 at 22:15
 */
class RoundDAL() {

  def getTemplateId():Long = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, template_id, description, fromTime, toTime, notes
            FROM rounds
            WHERE id = {round}
          """).on('round -> roundId)().map(row => Round(
          id = row[Long]("id"),
          description = row[Option[String]]("description").getOrElse(null),
          notes = row[Option[String]]("before_text").getOrElse(null),
          startTime = row[Date]("fromTime"), endTime=row[Date]("toTime")
        ))

        if (u.isEmpty) -1 else u.get.apply[Long]("template_id")
    }
  }

  def getRounds(): List[Round] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, template_id, description, fromTime, toTime, notes
            FROM rounds
            ORDER BY id DESC
          """)().map(row => Round(
          id = row[Long]("id"),
          description = row[Option[String]]("description").getOrElse(null),
          notes = row[Option[String]]("before_text").getOrElse(null),
          startTime = row[Date]("fromTime"), endTime=row[Date]("toTime")
        ))

        data.toList
    }
  }
}

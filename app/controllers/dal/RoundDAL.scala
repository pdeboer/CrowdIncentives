package controllers.dal

import play.api.db.DB
import controllers.{Round, TemplatePart}
import anorm._
import play.api.Play.current
import java.util.Date

/**
 * @author pdeboer
 *         First created on 09/12/13 at 22:15
 */
class RoundDAL() {

  def getRound(roundId: Int): Round = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, template_id, description, fromTime, toTime, notes
            FROM rounds
            WHERE id = {round}
          """).on('round -> roundId)().headOption

        if (data.isEmpty) null
        else Round(
          id = data.get.apply[Long]("id"),
          description = data.get.apply[Option[String]]("description").getOrElse(null),
          notes = data.get.apply[Option[String]]("before_text").getOrElse(null),
          startTime = data.get.apply[Date]("fromTime"), endTime = data.get.apply[Date]("toTime"), templateId = data.get.apply[Long]("template_id"))
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
          startTime = row[Date]("fromTime"), endTime = row[Date]("toTime"),
          templateId = row[Long]("template_id")
        ))

        data.toList
    }
  }
}

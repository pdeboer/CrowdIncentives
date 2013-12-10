package controllers.dal

import play.api.db.DB
import controllers.TemplatePart
import anorm._
import play.api.Play.current

/**
 * @author pdeboer
 *         First created on 09/12/13 at 22:15
 */
class TemplateDAL {
  val CURRENT_TEMPLATE_ID = 1

  def getParts(): List[TemplatePart] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, description, before_text, after_text
            FROM template_part
            WHERE template_id = {template}
          """).on('template -> CURRENT_TEMPLATE_ID)().map(row => TemplatePart(
          id = row[Long]("id"), name = row[String]("description"),
          beforeText = row[Option[String]]("before_text").getOrElse(null),
          afterText = row[Option[String]]("after_text").getOrElse(null)
        ))

        data.toList
    }
  }
}

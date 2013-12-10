package controllers.dal

import controllers.model.TemplatePart
import play.api.db.DB

/**
 * @author pdeboer
 *         First created on 09/12/13 at 22:15
 */
class TemplateDAL {
  val currentTemplate = 0;

  def getParts(): List[TemplatePart] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, name, before_text, after_text
            FROM template_part
            WHERE template_id = {template}
          """).on('template -> currentTemplate)().map(row => TemplatePart(
          id = row[Int]("id"), name = row[String]("name"),
          beforeText = row[Option[String]]("before_text").getOrElse(null),
          afterText = row[Option[String]]("after_text").getOrElse(null)
        ))
    }
  }
}

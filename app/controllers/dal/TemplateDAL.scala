package controllers.dal

import play.api.db.DB
import controllers.{Template, TemplatePart}
import anorm._
import play.api.Play.current

/**
 * @author pdeboer
 *         First created on 09/12/13 at 22:15
 */
class TemplateDAL(val roundId: Long) {

  def getTemplateId(): Long = {
    DB.withConnection {
      implicit c =>
        val u = SQL(
          """
            SELECT template_id FROM round WHERE id = {id}
          """).on('id -> roundId)().headOption

        if (u.isEmpty) -1 else u.get.apply[Long]("template_id")
    }
  }

  def getTemplate(templateId: Long): Template = {
    DB.withConnection {
      implicit c =>
        val u = SQL(
          """
            SELECT id, name, double_values_summed_up, global_has_multiple_parts,
              double_value_name
            FROM template WHERE id = {id}
          """).on('id -> templateId)().headOption

        if (u.isEmpty) null
        else Template(
          id = templateId,
          name = u.get.apply[String]("name"),
          doubleValuesSummed = u.get.apply[Boolean]("double_values_summed_up"),
          multiPartSelection = u.get.apply[Boolean]("global_has_multiple_parts"),
          doubleValueName = u.get.apply[Option[String]]("double_value_name").getOrElse(null)
        )
    }
  }

  def getPart(templatePartId: Long): TemplatePart = {
    DB.withConnection {
      implicit c =>
        val u = SQL(
          """
            SELECT id, description, before_text, after_text
            FROM template_part
            WHERE id={id}
          """).on('id -> templatePartId)().headOption

        if (u.isEmpty) null else TemplatePart(u.get.apply[Long]("id"), u.get.apply[String]("description"), u.get.apply[String]("before_text"), u.get.apply[String]("after_text"))
    }
  }

  def getParts(): List[TemplatePart] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT p.id, p.description, p.before_text, p.after_text
            FROM template_part p INNER JOIN round r ON r.template_id = p.template_id
            WHERE r.id = {round}
          """).on('round -> roundId)().map(row => TemplatePart(
          id = row[Long]("id"), name = row[String]("description"),
          beforeText = row[Option[String]]("before_text").getOrElse(null),
          afterText = row[Option[String]]("after_text").getOrElse(null)
        ))

        data.toList
    }
  }
}

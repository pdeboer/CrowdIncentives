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
            SELECT id, name, double_values_summed_up, global_has_multiple_parts, global_summary_title,
              double_value_name, global_name, global_description, global_link_name, part_link_prefix, online_display
            FROM template WHERE id = {id}
          """).on('id -> templateId)().headOption

        if (u.isEmpty) null
        else Template(
          id = templateId,
          name = u.get.apply[String]("name"),
          doubleValuesSummed = u.get.apply[Boolean]("double_values_summed_up"),
          multiPartSelection = u.get.apply[Boolean]("global_has_multiple_parts"),
          doubleValueName = u.get.apply[Option[String]]("double_value_name").getOrElse(null),
          globalName = u.get.apply[String]("global_name"),
          globalDescription = u.get.apply[String]("global_description"),
          globalLinkName = u.get.apply[String]("global_link_name"),
          partLinkPrefix = u.get.apply[String]("part_link_prefix"),
          onlineDisplay = u.get.apply[Boolean]("online_display"),
          globalSummaryTitle = u.get.apply[Option[String]]("global_summary_title").getOrElse(null)
        )
    }
  }

  def getPart(templatePartId: Long): TemplatePart = {
    DB.withConnection {
      implicit c =>
        val u = SQL(
          """
            SELECT id, description, before_text, after_text, description_in_global, short_text, title_for_url_field, title_for_image_field
            FROM template_part
            WHERE id={id}
          """).on('id -> templatePartId)().headOption

        if (u.isEmpty) null else TemplatePart(u.get.apply[Long]("id"), u.get.apply[String]("description"), u.get.apply[String]("before_text"), u.get.apply[String]("after_text"), descriptionForGlobal = u.get.apply[String]("description_in_global"), u.get.apply[String]("short_text"),
        titleForURLField = u.get.apply[Option[String]]("title_for_url_field").getOrElse(null),
        titleForImageField = u.get.apply[Option[String]]("title_for_image_field").getOrElse(null))
    }
  }

  def getParts(): List[TemplatePart] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT p.id, p.description, p.before_text, p.after_text, description_in_global, short_text, title_for_url_field,
                  title_for_image_field
            FROM template_part p INNER JOIN round r ON r.template_id = p.template_id
            WHERE r.id = {round}
          """).on('round -> roundId)().map(row => TemplatePart(
          id = row[Long]("id"), name = row[String]("description"),
          beforeText = row[Option[String]]("before_text").getOrElse(null),
          afterText = row[Option[String]]("after_text").getOrElse(null),
          descriptionForGlobal = row[String]("description_in_global"),
          shortText = row[String]("short_text"),
          titleForURLField = row[Option[String]]("title_for_url_field").getOrElse(null),
          titleForImageField = row[Option[String]]("title_for_image_field").getOrElse(null)
        ))

        data.toList
    }
  }
}

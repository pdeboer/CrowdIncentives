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

  def getRound(roundId: Long): Round = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, template_id, description, fromTime, toTime, notes
            FROM round
            WHERE id = {round}
          """).on('round -> roundId)().headOption

        if (data.isEmpty) null
        else Round(
          id = data.get.apply[Long]("id"),
          description = data.get.apply[Option[String]]("description").getOrElse(null),
          notes = data.get.apply[Option[String]]("notes").getOrElse(null),
          startTime = data.get.apply[Date]("fromTime"), endTime = data.get.apply[Date]("toTime"), templateId = data.get.apply[Long]("template_id"))
    }
  }

  def updateRound(round: Round) {
    DB.withConnection {
      implicit c =>

        SQL(
          """
            UPDATE round
            SET template_id={template}, description={description},
              fromTime={from}, toTime={to}, notes={notes}
            WHERE id={id}
          """).on('template -> round.templateId, 'description -> round.description,
            'from -> round.startTime, 'to -> round.endTime, 'notes -> round.notes, 'id -> round.id)
          .executeUpdate()
    }
  }

  def insertRound(round: Round, prevRoundID: String = null) = {
    DB.withConnection {
      implicit c =>

        val key: Option[Long] = SQL(
          """
            INSERT INTO round (template_id, description, fromTime, toTime, notes)
            VALUES ({template}, {description}, {from}, {to}, {notes})
          """).on('template -> round.templateId, 'description -> round.description,
            'from -> round.startTime, 'to -> round.endTime, 'notes -> round.notes, 'id -> round.id)
          .executeInsert()

        val created = key.get

        if (prevRoundID != null) {
          //transition codes to next round
          SQL("update codes set round_id={next} where code not in (select code from users)").on('next -> created).executeUpdate()

          val old = new StoryDAL(prevRoundID.toLong)
          val fresh = new StoryDAL(created)

          val templateOld = new TemplateDAL(prevRoundID.toLong)
          if (templateOld.getTemplateId() == round.templateId) {
            /**
             * copy parts first
             */
            //maps old part id to new ones
            var partMapping = collection.mutable.HashMap[Long, Long]()
            templateOld.getParts().foreach(templatePart => {
              val parts = old.getParts(templatePart.id)
              parts.foreach(storyPart => {
                val newKey = fresh.insertPart(templatePart.id, storyPart).get
                partMapping += storyPart.id -> newKey
              })
            })

            //copy integrated stories
            val integratedStories = old.getIntegratedStories()
            integratedStories.foreach(s => {
              val story = old.getIntegratedStory(s.id)

              val replacedStoryParts = story.parts.map(p => fresh.getPart(partMapping(p.id))).toList

              story.parts = replacedStoryParts
              fresh.insertIntegratedStory(story)
            })
          }

        }

        created
    }
  }

  def getRounds(): List[Round] = {
    DB.withConnection {
      implicit c =>
        val data = SQL(
          """
            SELECT id, template_id, description, fromTime, toTime, notes
            FROM round
            ORDER BY id DESC
          """)().map(row => Round(
          id = row[Long]("id"),
          description = row[Option[String]]("description").getOrElse(null),
          notes = row[Option[String]]("notes").getOrElse(null),
          startTime = row[Date]("fromTime"), endTime = row[Date]("toTime"),
          templateId = row[Long]("template_id")
        ))

        data.toList
    }
  }
}

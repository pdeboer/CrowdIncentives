package controllers.dal

import play.api.db.DB
import anorm._
import play.api.Play.current
import java.util.Date


/**
 * @author pdeboer
 *         First created on 10/12/13 at 18:02
 */
class ImageDAL() {
  def get(imageId: Long):Option[Array[Byte]] = {
    DB.withConnection {
      implicit c =>
        val image = SQL(
          """
            SELECT content FROM images WHERE id={id}
          """).on('id -> imageId)().headOption

        //if(image.isEmpty) None else Some(image.get.apply[Array[Byte]]("content"))
        None
    }
  }

  def insert(content:Array[Byte]) = {
    DB.withConnection {
      implicit c =>
        val id: Option[Long] = SQL(
          """
            INSERT INTO images (content)
            VALUES ({content}, {date})
          """
        ).on('content -> content, 'date->new Date())
          .executeInsert()

        id.get
    }
  }

  def delete(imageId:Long) {
    DB.withConnection {
      implicit c =>
      //update name
        SQL( """
          DELETE FROM images WHERE id={id}
             """).on('id -> imageId).executeUpdate()
    }
  }
}

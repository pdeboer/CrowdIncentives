package controllers.dal

import play.api.db.DB
import controllers.{Setting, TemplatePart}
import anorm._
import play.api.Play.current

/**
 * @author pdeboer
 *         First created on 09/12/13 at 22:15
 */
class SettingsDAL() {

  def get(key: String): Setting = {
    DB.withConnection {
      implicit c =>
        val u = SQL(
          """
            SELECT value FROM settings WHERE key={key}
          """).on('key -> key)().headOption

        if (u.isEmpty) null else Setting(key, u.get.apply[String]("value"))
    }
  }

  def set(setting: Setting) {
    DB.withConnection {
      implicit c =>
        SQL("DELETE FROM settings WHERE key = {key}").on('key -> setting.key).executeUpdate()

        SQL("INSERT INTO settings (key, value) VALUES ({key}, {value})").on('key -> setting.key, 'value -> setting.value).executeInsert()
    })
  }
}

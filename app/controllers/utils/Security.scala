package controllers.utils

import controllers.{TemplatePart, User}
import controllers.dal.{StoryDAL, TemplateDAL}

/**
 * @author pdeboer
 *         First created on 11/12/13 at 18:07
 */
object Security {
  def checkUserAccessToTemplatePart(u: User, t: TemplatePart) =
    new TemplateDAL(u.round).getParts().exists(_.id == t.id)

  def checkUserAllowedToEditPart(u: User, partId: Long) =
    new StoryDAL(u.round).getPart(partId).author.id == u.id
}

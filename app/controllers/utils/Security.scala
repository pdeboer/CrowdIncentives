package controllers.utils

import controllers.{TemplatePart, User}
import controllers.dal.{UserDAL, StoryDAL, TemplateDAL}

/**
 * @author pdeboer
 *         First created on 11/12/13 at 18:07
 */
object Security {
  def checkUserAccessToTemplatePart(u: User, t: TemplatePart) =
    new TemplateDAL(u.round).getParts().exists(_.id == t.id) || checkIsAdmin(u)

  def checkUserAllowedToEditPart(u: User, partId: Long) =
    new StoryDAL(u.round).getPart(partId).author.id == u.id || checkIsAdmin(u)

  def checkUserAllowedToViewGlobal(u: User, globalId: Long) =
    new StoryDAL(u.round).getIntegratedStories().exists(_.id == globalId) || checkIsAdmin(u)

  def checkUserAllowedToEditGlobal(u: User, globalId: Long) =
    new StoryDAL(u.round).getIntegratedStory(globalId).author.id == u.id || checkIsAdmin(u)

  def checkIfRedirectToWaitingRoom(u:User) = !new UserDAL().userRoundTimeframe(u).isNow && !checkIsAdmin(u)

  def checkIsAdmin(u:User) = u.isAdmin
}

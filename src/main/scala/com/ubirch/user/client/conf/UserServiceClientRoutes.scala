package com.ubirch.user.client.conf

import com.ubirch.user.client.conf.UserServiceClientConfig.host
import org.joda.time.{DateTime, DateTimeZone}

object UserServiceClientRoutes {

  val urlCheck = s"$host${UserServiceClientRouteKeys.pathCheck}"

  val urlDeepCheck = s"$host${UserServiceClientRouteKeys.pathDeepCheck}"

  def pathGroupMemberOf(
                         contextName: String,
                         providerId: String,
                         externalUserId: String
                       ): String = {

    val path = UserServiceClientRouteKeys.pathGroupMemberOf(
      contextName = contextName,
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathUserGET(
                   providerId: String,
                   externalUserId: String
                 ): String = {

    val path = UserServiceClientRouteKeys.pathUserFind(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  val pathUserPOST: String = s"$host${UserServiceClientRouteKeys.pathUser}"

  val pathUserRecreate: String = s"$host${UserServiceClientRouteKeys.pathRecreate}"

  def pathUserPUT(
                   providerId: String,
                   externalUserId: String
                 ): String = {

    val path = UserServiceClientRouteKeys.pathUserUpdate(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathUserDELETE(
                      providerId: String,
                      externalUserId: String
                    ): String = {

    val path = UserServiceClientRouteKeys.pathUserDelete(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathExternalIdExistsGET(externalId: String): String = {

    val path = UserServiceClientRouteKeys.pathExternalIdExists(externalId)
    s"$host$path"

  }

  val pathRegisterPOST = s"$host${UserServiceClientRouteKeys.pathRegister}"

  val pathActivationPOST = s"$host${UserServiceClientRouteKeys.pathActivation}"

  def pathUserInfoGET(
                       context: String,
                       providerId: String,
                       externalUserId: String
                     ): String = {

    val path = UserServiceClientRouteKeys.pathUserInfoGET(context, providerId, externalUserId)
    s"$host$path"

  }

  val pathUserInfoPUT = s"$host${UserServiceClientRouteKeys.pathUserInfoPUT}"

  def pathGetUsers(
      limit: Int,
      lastCreatedAtOpt: Option[DateTime]
                  ): String = {
    val path = UserServiceClientRouteKeys.pathUsers
    lastCreatedAtOpt match {
      case Some(lastCreatedAt) => s"$host$path?limit=$limit&lastCreatedAt=${lastCreatedAt.toDateTime(DateTimeZone.UTC).toString}"
      case None => s"$host$path?limit=$limit"
    }
  }
}

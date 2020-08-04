package com.ubirch.user.client.conf

import com.ubirch.user.client.conf.UserClientConfig.host

object UserClientRoutes {

  val urlCheck = s"$host${UserClientRouteKeys.pathCheck}"

  val urlDeepCheck = s"$host${UserClientRouteKeys.pathDeepCheck}"

  def pathGroupMemberOf(
                         contextName: String,
                         providerId: String,
                         externalUserId: String
                       ): String = {

    val path = UserClientRouteKeys.pathGroupMemberOf(
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

    val path = UserClientRouteKeys.pathUserFind(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  val pathUserPOST: String = s"$host${UserClientRouteKeys.pathUser}"

  val pathUserRecreate: String = s"$host${UserClientRouteKeys.pathRecreate}"

  def pathUserPUT(
                   providerId: String,
                   externalUserId: String
                 ): String = {

    val path = UserClientRouteKeys.pathUserUpdate(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathUserDELETE(
                      providerId: String,
                      externalUserId: String
                    ): String = {

    val path = UserClientRouteKeys.pathUserDelete(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathExternalIdExistsGET(externalId: String): String = {

    val path = UserClientRouteKeys.pathExternalIdExists(externalId)
    s"$host$path"

  }

  val pathRegisterPOST = s"$host${UserClientRouteKeys.pathRegister}"

  def pathUserInfoGET(
                       context: String,
                       providerId: String,
                       externalUserId: String
                     ): String = {

    val path = UserClientRouteKeys.pathUserInfoGET(context, providerId, externalUserId)
    s"$host$path"

  }

  val pathUserInfoPUT = s"$host${UserClientRouteKeys.pathUserInfoPUT}"

}

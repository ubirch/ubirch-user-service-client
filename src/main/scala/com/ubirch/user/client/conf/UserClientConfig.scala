package com.ubirch.user.client.conf

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-05-15
  */
object UserClientConfig extends ConfigBase {

  /**
    * The host the REST API runs on.
    *
    * @return host
    */
  private def host = config.getString(UserClientConfigKeys.HOST)

  val urlCheck = s"$host${UserClientConfigKeys.pathCheck}"

  val urlDeepCheck = s"$host${UserClientConfigKeys.pathDeepCheck}"

  def pathGroupMemberOf(contextName: String,
                        providerId: String,
                        externalUserId: String
                       ): String = {

    val path = UserClientConfigKeys.pathGroupMemberOf(
      contextName = contextName,
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathUserGET(providerId: String,
                  externalUserId: String
                 ): String = {

    val path = UserClientConfigKeys.pathUserFind(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  val pathUserPOST: String = s"$host${UserClientConfigKeys.pathUser}"

  val pathUserRecreate: String = s"$host${UserClientConfigKeys.pathRecreate}"

  def pathUserPUT(providerId: String,
                  externalUserId: String
                 ): String = {

    val path = UserClientConfigKeys.pathUserUpdate(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathUserDELETE(providerId: String,
                     externalUserId: String
                    ): String = {

    val path = UserClientConfigKeys.pathUserDelete(
      providerId = providerId,
      externalUserId = externalUserId
    )

    s"$host$path"

  }

  def pathExternalIdExistsGET(externalId: String): String = {

    val path = UserClientConfigKeys.pathExternalIdExists(externalId)
    s"$host$path"

  }

  val pathRegisterPOST = s"$host${UserClientConfigKeys.pathRegister}"

  def pathUserInfoGET(context: String,
                      providerId: String,
                      externalUserId: String
                     ): String = {

    val path = UserClientConfigKeys.pathUserInfoGET(context, providerId, externalUserId)
    s"$host$path"

  }

  val pathUserInfoPUT = s"$host${UserClientConfigKeys.pathUserInfoPUT}"

}

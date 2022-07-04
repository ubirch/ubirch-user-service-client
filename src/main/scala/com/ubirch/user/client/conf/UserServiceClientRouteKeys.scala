package com.ubirch.user.client.conf

import java.net.URLEncoder

object UserServiceClientRouteKeys {

  final val apiPrefix = "api"
  final val serviceName = "userService"
  final val currentVersion = "v1"

  final val check = "check"
  final val deepCheck = "deepCheck"
  final val user = "user"
  final val users = "users"
  final val group = "group"
  final val memberOf = "memberOf"
  final val externalIdExists = "extIdExists"
  final val recreate = "recreate"
  final val info = "info"
  final val register = "register"
  final val activation = "activation"
  val pathPrefix = s"/$apiPrefix/$serviceName/$currentVersion"
  val pathCheck = s"$pathPrefix/$check"
  val pathDeepCheck = s"$pathPrefix/$deepCheck"
  val pathUser = s"$pathPrefix/$user"
  val pathRecreate = s"$pathPrefix/$user/$recreate"
  final val pathActivation = s"$pathPrefix/$user/$activation"
  final val pathUserInfo = s"$pathUser/$info"
  final val pathUserInfoPUT = pathUserInfo
  final val pathRegister = s"$pathPrefix/$register"
  final val pathUsers = s"$pathPrefix/$users"

  def pathUserFind(providerId: String, externalUserId: String): String = {

    val providerEncoded = URLEncoder.encode(providerId, "UTF-8")
    val userEncoded = URLEncoder.encode(externalUserId, "UTF-8")

    s"$pathUser/$providerEncoded/$userEncoded"
  }

  def pathUserUpdate(providerId: String, externalUserId: String): String = {

    val providerEncoded = URLEncoder.encode(providerId, "UTF-8")
    val userEncoded = URLEncoder.encode(externalUserId, "UTF-8")

    s"$pathUser/$providerEncoded/$userEncoded"

  }

  def pathUserDelete(providerId: String, externalUserId: String): String = {

    val providerEncoded = URLEncoder.encode(providerId, "UTF-8")
    val userEncoded = URLEncoder.encode(externalUserId, "UTF-8")

    s"$pathUser/$providerEncoded/$userEncoded"

  }

  def pathExternalIdExists(externalId: String): String = {

    val userEncoded = URLEncoder.encode(externalId, "UTF-8")

    s"$pathUser/$externalIdExists/$userEncoded"

  }

  def pathGroupMemberOf(
                         contextName: String,
                         providerId: String,
                         externalUserId: String
                       ): String = {

    val contextEncoded = URLEncoder.encode(contextName, "UTF-8")
    val providerEncoded = URLEncoder.encode(providerId, "UTF-8")
    val userEncoded = URLEncoder.encode(externalUserId, "UTF-8")

    s"$pathPrefix/$group/$memberOf/$contextEncoded/$providerEncoded/$userEncoded"

  }

  final def pathUserInfoGET(context: String, providerId: String, userId: String): String = {

    val contextEncoded = URLEncoder.encode(context, "UTF-8")
    val providerEncoded = URLEncoder.encode(providerId, "UTF-8")
    val userEncoded = URLEncoder.encode(userId, "UTF-8")

    s"$pathUserInfo/$contextEncoded/$providerEncoded/$userEncoded"
  }

}

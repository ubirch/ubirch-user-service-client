package com.ubirch.user.client.model

case class UserUpdate(displayName: String)

case class UpdateInfo(simpleUserContext: SimpleUserContext, update: UserUpdate)

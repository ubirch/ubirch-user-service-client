package com.ubirch.user.client.model

import akka.http.scaladsl.model.StatusCode
import org.joda.time.DateTime

case class ActivationUpdate(updates: Seq[UserActivationUpdate])

case class UserActivationUpdate(externalId: String, activate: Boolean, executionDate: Option[DateTime] = None)

case class ActivationResponse(status: StatusCode, response: String)
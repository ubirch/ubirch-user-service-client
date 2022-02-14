package com.ubirch.user.client.model

import java.util.UUID
import org.joda.time.DateTime

case class User(
                 id: Option[UUID] = None,
                 displayName: String,
                 providerId: String,
                 externalId: String,
                 email: Option[String] = None,
                 activeUser: Boolean = false,
                 locale: String,
                 action: Option[Action] = None,
                 executionDate: Option[DateTime] = None,
                 created: DateTime = DateTime.now(),
                 updated: DateTime = DateTime.now()
               )


sealed trait Action

case object Activate extends Action

case object Deactivate extends Action

object Action {

  def unsafeFromString(value: String): Action = value.toUpperCase match {
    case "ACTIVATE" => Activate
    case "DEACTIVATE" => Deactivate
  }

  def toFormattedString(status: Action): String = status match {
    case Activate => "ACTIVATE"
    case Deactivate => "DEACTIVATE"
  }

}
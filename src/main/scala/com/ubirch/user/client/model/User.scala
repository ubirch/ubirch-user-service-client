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
                 created: DateTime = DateTime.now(),
                 updated: DateTime = DateTime.now()
               )

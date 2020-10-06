package com.ubirch.user.client.model

import java.util.UUID

/**
  * author: cvandrei
  * since: 2017-03-29
  */
case class Context(
                    id: Option[UUID] = None,
                    displayName: String
                  )

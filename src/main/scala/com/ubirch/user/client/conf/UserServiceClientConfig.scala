package com.ubirch.user.client.conf

import com.ubirch.util.config.ConfigBase

object UserServiceClientConfig extends ConfigBase {

  /**
    * The host the REST API runs on.
    *
    * @return host
    */
  def host: String = config.getString(UserServiceClientConfigKeys.HOST)

}

package com.ubirch.user.client.conf

import com.ubirch.util.config.ConfigBase

object UserClientConfig extends ConfigBase {

  /**
    * The host the REST API runs on.
    *
    * @return host
    */
  def host: String = config.getString(UserClientConfigKeys.HOST)

}

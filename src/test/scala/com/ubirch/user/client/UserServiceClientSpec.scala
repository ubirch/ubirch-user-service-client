package com.ubirch.user.client

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.user.client.UserServiceClient.userInfoGET
import com.ubirch.user.client.model._
import org.scalatest.{AsyncFeatureSpec, Matchers}

class UserServiceClientSpec extends AsyncFeatureSpec with Matchers with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("userServiceClientTSpec")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val httpClient: HttpExt = Http()

  feature("user service checks") {

    scenario("check") {

      UserServiceClient.check() map { jsonResponseOpt =>
        jsonResponseOpt.nonEmpty shouldBe true
        jsonResponseOpt.get.status shouldBe "OK"
      }
    }

    scenario("deepCheck") {

      UserServiceClient.deepCheck() map { deepCheckResponse =>
        deepCheckResponse.status shouldBe true
      }
    }
  }

  feature("user client requests") {

    // contextName, providerId and externalUserId have been created by InitData
    //    val contextName = "ubirch-local"
    val contextName = "trackle-dev"

    val providerId = "google"
    val externalUserId = "1234"
    val userName = "some user name"
    val newUserName = "new user name"
    val locale = "en-US"
    val email = Some("someUser@ubirch.com")

    scenario("post user") {

      val userContext = UserContext(
        context = contextName,
        providerId = providerId,
        externalUserId = externalUserId,
        userName = userName,
        locale = locale,
        email = email
      )

      UserServiceClient.registerPOST(userContext)
        .map { result =>
          result.nonEmpty shouldBe true
        }
    }

    scenario("userInfoGET") {
      userInfoGET(contextName, providerId, externalUserId)
        .map { result =>
          result.nonEmpty shouldBe true
          result.get.isInstanceOf[UserInfo] shouldBe true
          val userInfo = result.get
          userInfo.locale shouldBe locale
        }

    }

    scenario("user Get") {

      UserServiceClient.userGET(
        providerId = providerId,
        externalUserId = externalUserId
      ).map { futureUserOpt =>
        futureUserOpt.nonEmpty shouldBe true
      }
    }

    scenario("userInfoPUT") {

      val updateInfo = UpdateInfo(
        SimpleUserContext(
          context = contextName,
          providerId = providerId,
          userId = externalUserId
        ),
        UserUpdate(newUserName)
      )

      UserServiceClient.userInfoPUT(updateInfo)
        .map { userInfoOpt =>
          userInfoOpt.nonEmpty shouldBe true
          val userInfo = userInfoOpt.get
          userInfo.displayName shouldBe newUserName
        }

    }


    scenario("externalId exists") {

      UserServiceClient.extIdExistsGET(externalUserId)
        .map { boolean => boolean shouldBe true }
    }


    scenario("userInfoDELETE") {

      UserServiceClient.userDELETE(
        providerId = providerId,
        externalUserId = externalUserId
      ).map { success =>
        success shouldBe true
      }
    }


    scenario("groupMemberOff") {

      UserServiceClient.groupMemberOf(
        contextName = contextName,
        providerId = providerId,
        externalUserId = externalUserId
      ).map { groupSet =>
        groupSet.nonEmpty shouldBe true
      }
    }

  }


}
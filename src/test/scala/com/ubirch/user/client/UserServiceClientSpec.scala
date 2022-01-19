package com.ubirch.user.client

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.user.client.UserServiceClient.userInfoGET
import com.ubirch.user.client.model._
import org.scalatest.featurespec.AsyncFeatureSpec
import org.scalatest.matchers.should.Matchers

class UserServiceClientSpec extends AsyncFeatureSpec with Matchers with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("userServiceClientTSpec")
  implicit val httpClient: HttpExt = Http()

  Feature("user service checks") {

    Scenario("check") {

      UserServiceClient.check() map { jsonResponseOpt =>
        jsonResponseOpt.nonEmpty shouldBe true
        jsonResponseOpt.get.status shouldBe "OK"
      }
    }

    Scenario("deepCheck") {

      UserServiceClient.deepCheck() map { deepCheckResponse =>
        deepCheckResponse.status shouldBe true
      }
    }
  }

  Feature("user client requests") {

    val contextName = "trackle-dev"

    val providerId = "google"
    val externalUserId = "1234"
    val userName = "some user name"
    val newUserName = "new user name"
    val locale = "en-US"
    val email = Some("someUser@ubirch.com")

    Scenario("post user") {

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

    Scenario("userInfoGET") {
      userInfoGET(contextName, providerId, externalUserId)
        .map { result =>
          result.nonEmpty shouldBe true
          result.get.isInstanceOf[UserInfo] shouldBe true
          val userInfo = result.get
          userInfo.locale shouldBe locale
        }

    }

    Scenario("user Get") {

      UserServiceClient.userGET(
        providerId = providerId,
        externalUserId = externalUserId
      ).map { futureUserOpt =>
        futureUserOpt.nonEmpty shouldBe true
      }
    }

    //    Scenario("userInfoPUT") {
    //
    //      val updateInfo = UpdateInfo(
    //        SimpleUserContext(
    //          context = contextName,
    //          providerId = providerId,
    //          userId = externalUserId
    //        ),
    //        UserUpdate(newUserName)
    //      )
    //
    //      UserServiceClient.userInfoPUT(updateInfo)
    //        .map { userInfoOpt =>
    //          userInfoOpt.nonEmpty shouldBe true
    //          val userInfo = userInfoOpt.get
    //          userInfo.displayName shouldBe newUserName
    //        }
    //
    //    }

    Scenario("groupMemberOff returns group") {
      UserServiceClient.groupMemberOf(
        contextName = contextName,
        providerId = providerId,
        externalUserId = externalUserId
      ).map { groupSetOpt =>
        groupSetOpt.nonEmpty shouldBe true
        groupSetOpt.get.nonEmpty shouldBe true
      }
    }

    Scenario("externalId exists") {

      UserServiceClient.extIdExistsGET(externalUserId)
        .map { boolean => boolean shouldBe true }
    }

    Scenario("userInfoDELETE") {

      UserServiceClient.userDELETE(
        providerId = providerId,
        externalUserId = externalUserId
      ).map { success =>
        success shouldBe true
      }
    }

    Scenario("groupMemberOff returns empty set") {
      UserServiceClient.groupMemberOf(
        contextName = contextName,
        providerId = providerId,
        externalUserId = externalUserId
      ).map { groupSetOpt =>
        groupSetOpt.nonEmpty shouldBe true
        groupSetOpt.get.nonEmpty shouldBe false
      }
    }

  }

}

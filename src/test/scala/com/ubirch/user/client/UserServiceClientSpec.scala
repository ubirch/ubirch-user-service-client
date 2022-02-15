package com.ubirch.user.client

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK}
import akka.http.scaladsl.{Http, HttpExt}
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.user.client.UserServiceClient.userInfoGET
import com.ubirch.user.client.model._
import org.joda.time.{DateTime, DateTimeZone}
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
        futureUserOpt.isDefined shouldBe true
        val user = futureUserOpt.get
        user.id.isDefined shouldBe true
        user.providerId shouldBe providerId
        user.externalId shouldBe externalUserId
        user.email shouldBe email.map(_.toLowerCase())
        user.activeUser shouldBe false
        user.locale shouldBe locale
        user.action shouldBe None
        user.executionDate shouldBe None
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

    Scenario("update active state") {

      UserServiceClient.activationPOST(
      ActivationUpdate(Seq(UserActivationUpdate(externalId = externalUserId, activate = true)))
      ).map {
        case Right(result: ActivationResponse) => result.status shouldBe OK
        case Left(_) => fail("user activation should be successfully processed")
      }
    }

    Scenario("fail to update active state") {

      UserServiceClient.activationPOST(
        ActivationUpdate(Seq(UserActivationUpdate(externalId = externalUserId, activate = true)))
      ).map {
        case Right(result: ActivationResponse) =>
          result.response.contains(s"$externalUserId;true;;update not possible due to target status of activeUser " +
            s"flag already being 'true'.") shouldBe true
          result.status shouldBe BadRequest
        case Left(_) => fail("user activation should be successfully processed")
      }
    }

    Scenario("update active state in future") {

      val executionDate = DateTime.now(DateTimeZone.UTC).plusDays(1)
      UserServiceClient.activationPOST(
        ActivationUpdate(Seq(UserActivationUpdate(externalId = externalUserId, activate = false, Some(executionDate) )))
      ).flatMap {

        case Right(result: ActivationResponse) =>

          result.status shouldBe OK

          UserServiceClient.userGET(
            providerId = providerId,
            externalUserId = externalUserId
          ).map { futureUserOpt =>
            futureUserOpt.nonEmpty shouldBe true
            val user: User = futureUserOpt.get
            user.activeUser shouldBe true
            user.providerId shouldBe providerId
            user.locale shouldBe locale
            println()
            user.executionDate shouldBe Some(executionDate)
            user.action shouldBe Some(Deactivate)
          }

        case Left(_) => fail("user activation should be successfully processed")
      }
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

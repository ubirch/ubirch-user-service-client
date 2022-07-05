package com.ubirch.user.client

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.user.client.conf.UserServiceClientRoutes
import com.ubirch.user.client.formats.UserFormats
import com.ubirch.user.client.model._
import com.ubirch.util.deepCheck.model.DeepCheckResponse
import com.ubirch.util.deepCheck.util.DeepCheckResponseUtil
import com.ubirch.util.json.{Json4sUtil, JsonFormats, MyJsonProtocol}
import com.ubirch.util.model.JsonResponse
import org.joda.time.DateTimeZone
import org.json4s.Formats
import org.json4s.native.Serialization.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object UserServiceClient extends MyJsonProtocol with StrictLogging {

  override implicit def json4sJacksonFormats: Formats = JsonFormats.default ++ UserFormats.all
  def check()(implicit httpClient: HttpExt, materializer: Materializer): Future[Option[JsonResponse]] = {

    val url = UserServiceClientRoutes.urlCheck
    httpClient.singleRequest(HttpRequest(uri = url)) flatMap {

      case HttpResponse(StatusCodes.OK, _, entity, _) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
          Some(read[JsonResponse](body.utf8String))
        }

      case res@HttpResponse(code, _, _, _) =>

        res.discardEntityBytes()
        Future.successful(
          logErrorAndReturnNone(s"check() call to key-service failed: url=$url code=$code, status=${res.status}")
        )

    }

  }

  def deepCheck()(implicit httpClient: HttpExt, materializer: Materializer): Future[DeepCheckResponse] = {

    val statusCodes: Set[StatusCode] = Set(StatusCodes.OK, StatusCodes.ServiceUnavailable)

    val url = UserServiceClientRoutes.urlDeepCheck

    httpClient.singleRequest(HttpRequest(uri = url)) flatMap {

      case HttpResponse(status, _, entity, _) if statusCodes.contains(status) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
          read[DeepCheckResponse](body.utf8String)
        }

      case res@HttpResponse(code, _, _, _) =>

        res.discardEntityBytes()
        val errorText = s"deepCheck() call to user-service failed: url=$url code=$code, status=${res.status}"
        logger.error(errorText)
        val deepCheckRes = DeepCheckResponse(status = false, messages = Seq(errorText))
        Future.successful(
          DeepCheckResponseUtil.addServicePrefix("user-service", deepCheckRes)
        )

    }

  }

  def groupMemberOf(
                     contextName: String,
                     providerId: String,
                     externalUserId: String
                   )
                   (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[Set[Group]]] = {

    logger.debug("groups(): query groups through REST API")
    val url = UserServiceClientRoutes.pathGroupMemberOf(
      contextName = contextName,
      providerId = providerId,
      externalUserId = externalUserId
    )

    httpClient.singleRequest(HttpRequest(uri = url)) flatMap {

      case HttpResponse(StatusCodes.OK, _, entity, _) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
          Some(read[Set[Group]](body.utf8String))
        }

      case res@HttpResponse(code, _, _, _) =>

        res.discardEntityBytes()
        Future.successful(
          logErrorAndReturnNone(s"groups() call to user-service REST API failed: url=$url, code=$code")
        )

    }

  }

  def userGET(
               providerId: String,
               externalUserId: String
             )(implicit httpClient: HttpExt, materializer: Materializer): Future[Option[User]] = {

    logger.debug("userGET(): query user through REST API")
    val url = UserServiceClientRoutes.pathUserGET(
      providerId = providerId,
      externalUserId = externalUserId
    )

    httpClient.singleRequest(HttpRequest(uri = url)) flatMap {

      case HttpResponse(StatusCodes.OK, _, entity, _) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
          Some(read[User](body.utf8String))
        }

      case res@HttpResponse(code, _, _, _) =>

        res.discardEntityBytes()
        logger.warn(s"userGET() call to user-service REST API failed: url=$url, code=$code")
        Future.successful(None)

    }

  }

  def userPOST(user: User)
              (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[User]] = {

    Json4sUtil.any2String(user) match {

      case Some(userJsonString: String) =>

        logger.debug(s"user (object): $userJsonString")
        val url = UserServiceClientRoutes.pathUserPOST
        postUserHelperMethod(url, userJsonString)

      case None =>
        logger.error(s"failed to to convert input to JSON: user=$user")
        Future.successful(None)

    }
  }

  def userRecreate(user: User)
                  (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[User]] = {

    Json4sUtil.any2String(user) match {

      case Some(userJsonString: String) =>

        logger.debug(s"recreate: $userJsonString")
        val url = UserServiceClientRoutes.pathUserRecreate
        postUserHelperMethod(url, userJsonString)

      case None =>
        logger.error(s"failed to to convert input to JSON: user=$user")
        Future.successful(None)

    }
  }

  private def postUserHelperMethod(url: Uri, userJsonString: String)
                                  (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[User]] = {
    val req = HttpRequest(
      method = HttpMethods.POST,
      uri = url,
      entity = HttpEntity.Strict(ContentTypes.`application/json`, data = ByteString(userJsonString))
    )
    httpClient.singleRequest(req) flatMap {

      case HttpResponse(StatusCodes.OK, _, entity, _) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
          Some(read[User](body.utf8String))
        }

      case res@HttpResponse(code, _, _, _) =>

        res.discardEntityBytes()
        Future.successful(
          logErrorAndReturnNone(s"userPOST() call to user-service failed: url=$url code=$code, status=${res.status}")
        )
    }
  }

  def userPUT(user: User)
             (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[User]] = {

    Json4sUtil.any2String(user) match {

      case Some(userJsonString: String) =>

        logger.debug(s"user (object): $userJsonString")
        val url = UserServiceClientRoutes.pathUserPUT(
          providerId = user.providerId,
          externalUserId = user.externalId
        )
        val req = HttpRequest(
          method = HttpMethods.PUT,
          uri = url,
          entity = HttpEntity.Strict(ContentTypes.`application/json`, data = ByteString(userJsonString))
        )
        httpClient.singleRequest(req) flatMap {

          case HttpResponse(StatusCodes.OK, _, entity, _) =>

            entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
              Some(read[User](body.utf8String))
            }

          case res@HttpResponse(code, _, _, _) =>

            res.discardEntityBytes()
            Future.successful(
              logErrorAndReturnNone(s"userPOST() call to user-service failed: url=$url code=$code, status=${res.status}")
            )

        }

      case None =>
        logger.error(s"failed to to convert input to JSON: user=$user")
        Future(None)

    }
  }

  def userDELETE(
                  providerId: String,
                  externalUserId: String
                )(implicit httpClient: HttpExt, materializer: Materializer): Future[Boolean] = {

    logger.debug("userDELETE(): delete user through REST API")
    val url = UserServiceClientRoutes.pathUserDELETE(
      providerId = providerId,
      externalUserId = externalUserId
    )

    httpClient.singleRequest(HttpRequest(uri = url, method = HttpMethods.DELETE)) map {

      case res@HttpResponse(StatusCodes.OK, _, _, _) =>

        res.discardEntityBytes()
        true

      case res@HttpResponse(code, _, _, _) =>

        res.discardEntityBytes()
        logErrorAndReturnNone(s"userGET() call to user-service REST API failed: url=$url, code=$code")
        false

    }

  }

  def extIdExistsGET(externalId: String)
                    (implicit httpClient: HttpExt, materializer: Materializer): Future[Boolean] = {

    logger.debug("extIdExistsGET(): search external ID through REST API")
    val url = UserServiceClientRoutes.pathExternalIdExistsGET(externalId)

    httpClient.singleRequest(HttpRequest(uri = url)) map {

      case res@HttpResponse(StatusCodes.OK, _, _, _) =>

        res.discardEntityBytes()
        true

      case _@HttpResponse(code, _, _, _) =>

        logger.warn(s"emailExistsGET() call to user-service REST API failed: url=$url, code=$code")
        false

    }

  }

  def registerPOST(userContext: UserContext)
                  (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[UserInfo]] = {

    Json4sUtil.any2String(userContext) match {

      case Some(userContextJsonString: String) =>

        logger.debug(s"userContext (object): $userContextJsonString")
        val url = UserServiceClientRoutes.pathRegisterPOST
        val req = HttpRequest(
          method = HttpMethods.POST,
          uri = url,
          entity = HttpEntity.Strict(ContentTypes.`application/json`, data = ByteString(userContextJsonString))
        )
        httpClient.singleRequest(req) flatMap {

          case HttpResponse(StatusCodes.OK, _, entity, _) =>

            entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
              Some(read[UserInfo](body.utf8String))
            }

          case res@HttpResponse(code, _, _, _) =>

            res.discardEntityBytes()
            Future.successful(
              logErrorAndReturnNone(s"registerPOST() call to user-service failed: url=$url code=$code, status=${res.status}, userContextJson=$userContextJsonString")
            )

        }

      case None =>
        logger.error(s"failed to to convert input to JSON: userContext=$userContext")
        Future.successful(None)

    }

  }

  def activationPOST(activation: ActivationUpdate)
                  (implicit httpClient: HttpExt, materializer: Materializer): Future[Either[String, ActivationResponse]] = {

    Json4sUtil.any2String(activation) match {

      case Some(activationJson: String) =>

        val url = UserServiceClientRoutes.pathActivationPOST
        val req = HttpRequest(
          method = HttpMethods.POST,
          uri = url,
          entity = HttpEntity.Strict(ContentTypes.`application/json`, data = ByteString(activationJson))
        )
        httpClient.singleRequest(req) flatMap { rsp =>

          rsp.entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { csvBody =>
              Right(ActivationResponse(rsp.status, csvBody.utf8String))
            }
        }

      case None =>
        Future.successful(Left(s"failed to to convert input to JSON: activationJson=$activation"))
    }
  }


  def userInfoGET(
                   context: String,
                   providerId: String,
                   externalUserId: String
                 )
                 (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[UserInfo]] = {

    logger.debug(s"userInfoGET(): search userInfo ($context/$providerId/$externalUserId) through REST API")
    val url = UserServiceClientRoutes.pathUserInfoGET(context = context, providerId = providerId, externalUserId = externalUserId)

    httpClient.singleRequest(HttpRequest(uri = url)) flatMap {

      case HttpResponse(StatusCodes.OK, _, entity, _) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
          Some(read[UserInfo](body.utf8String))
        }

      case _@HttpResponse(code, _, _, _) =>

        logErrorAndReturnNone(s"userInfoGET() call to user-service REST API failed: url=$url, code=$code")
        Future.successful(None)

    }

  }

  @deprecated("this endpoint seems broken", "since ubirch-user-service-client 1.0.4")
  def userInfoPUT(updateInfo: UpdateInfo)
                 (implicit httpClient: HttpExt, materializer: Materializer): Future[Option[UserInfo]] = {

    Json4sUtil.any2String(updateInfo) match {

      case Some(updateInfoJsonString: String) =>

        logger.debug(s"updateInfo (object): $updateInfoJsonString")
        val url = UserServiceClientRoutes.pathUserInfoPUT
        val req = HttpRequest(
          method = HttpMethods.PUT,
          uri = url,
          entity = HttpEntity.Strict(ContentTypes.`application/json`, data = ByteString(updateInfoJsonString))
        )
        httpClient.singleRequest(req) flatMap {

          case HttpResponse(StatusCodes.OK, _, entity, _) =>

            entity.dataBytes.runFold(ByteString(""))(_ ++ _) map { body =>
              Some(read[UserInfo](body.utf8String))
            }

          case res@HttpResponse(code, _, _, _) =>

            res.discardEntityBytes()
            Future.successful(
              logErrorAndReturnNone(s"userInfoPUT() call to user-service failed: url=$url code=$code, status=${res.status}")
            )

        }

      case None =>
        logger.error(s"failed to to convert input to JSON: updateInfo=$updateInfo")
        Future.successful(None)

    }

  }

  def getUsersWithPagination(limit: Int, lastCreatedAtOpt: Option[org.joda.time.DateTime])(implicit httpClient: HttpExt, materializer: Materializer): Future[List[User]] = {
    logger.debug(s"getUsersWithPagination(): get users limit: $limit, lastCreatedAt: ${lastCreatedAtOpt.map(_.toDateTime(DateTimeZone.UTC))} through REST API")
    val url = UserServiceClientRoutes.pathGetUsers(limit, lastCreatedAtOpt)

    httpClient.singleRequest(HttpRequest(uri = url)) flatMap {

      case HttpResponse(StatusCodes.OK, _, entity, _) =>

        entity.dataBytes.runFold(ByteString(""))(_ ++ _) flatMap { body =>
          Future.fromTry(
            Try(read[List[User]](body.utf8String))
          )
        }

      case _@HttpResponse(code, _, _, _) =>

        logErrorAndReturnNone(s"getUsersWithPagination() call to user-service REST API failed: url=$url, code=$code")
        Future.failed(UserServiceClientException("fail to get users with pagination"))

    }
  }

  private def logErrorAndReturnNone[T](
                                        errorMsg: String,
                                        t: Option[Throwable] = None
                                      ): Option[T] = {
    t match {
      case None => logger.error(errorMsg)
      case Some(someThrowable: Throwable) => logger.error(errorMsg, someThrowable)
    }

    None

  }

}

case class UserServiceClientException(message: String) extends Exception(message)

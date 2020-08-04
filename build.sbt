//concurrentRestrictions in Global := Seq(
//  Tags.limit(Tags.Test, 1)
//)
//
///*
// * MODULES
// ********************************************************/
//
//lazy val userServiceClient = (project in file("."))
//  .settings(
//    name := "ubirch-user-service-client",
//    description := "REST client of the user-service",
//    scalaVersion := "2.11.12",
//    scalacOptions ++= Seq("-feature"),
//    organization := "com.ubirch.user",
//    homepage := Some(url("http://ubirch.com")),
//    scmInfo := Some(ScmInfo(
//      url("https://github.com/ubirch/ubirch-user-service-client"),
//      "scm:git:git@github.com:ubirch/ubirch-user-service-client.git"
//    )),
//    version := "1.0.4",
//    resolvers ++= Seq(
//      Resolver.sonatypeRepo("releases"),
//      Resolver.sonatypeRepo("snapshots")
//    ),
//    (sys.env.get("CLOUDREPO_USER"), sys.env.get("CLOUDREPO_PW")) match {
//      case (Some(username), Some(password)) =>
//        println("USERNAME and/or PASSWORD found.")
//        credentials += Credentials("ubirch.mycloudrepo.io", "ubirch.mycloudrepo.io", username, password)
//      case _ =>
//        println("USERNAME and/or PASSWORD is taken from /.sbt/.credentials.")
//        credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
//    },
//    publishTo := Some("io.cloudrepo" at "https://ubirch.mycloudrepo.io/repositories/trackle-mvn"),
//    publishMavenStyle := true,
//    libraryDependencies ++= depClientRest
//  )
//
//
///*
// * MODULE DEPENDENCIES
// ********************************************************/
//
//lazy val depClientRest = Seq(
//  akkaHttp,
//  akkaStream,
//  json4sNative,
//  ubirchResponse,
//  ubirchDeepCheckModel,
//  ubirchConfig,
//  scalatest % "test"
//) ++ scalaLogging
//
//
///*
// * DEPENDENCIES
// ********************************************************/
//
//// VERSIONS
//val akkaV = "2.5.11"
//val akkaHttpV = "10.1.3"
//val json4sV = "3.6.0"
//
//val scalaTestV = "3.0.1"
//
//lazy val logbackV = "1.2.3"
//lazy val logbackESV = "1.5"
//lazy val slf4jV = "1.7.25"
//lazy val log4jV = "2.9.1"
//lazy val scalaLogV = "3.7.2"
//lazy val scalaLogSLF4JV = "2.1.2"
//
//
//// GROUP NAMES
//val ubirchUtilG = "com.ubirch.util"
//val json4sG = "org.json4s"
//val akkaG = "com.typesafe.akka"
//val slf4jG = "org.slf4j"
//val typesafeLoggingG = "com.typesafe.scala-logging"
//val logbackG = "ch.qos.logback"
//
//lazy val scalatest = "org.scalatest" %% "scalatest" % scalaTestV
//
//lazy val json4sNative = json4sG %% "json4s-native" % json4sV
//
//lazy val scalaLogging = Seq(
//  slf4jG % "slf4j-api" % slf4jV,
//  slf4jG % "log4j-over-slf4j" % slf4jV,
//  slf4jG % "jul-to-slf4j" % slf4jV,
//  logbackG % "logback-core" % logbackV,
//  logbackG % "logback-classic" % logbackV,
//  "net.logstash.logback" % "logstash-logback-encoder" % "5.0",
//  typesafeLoggingG %% "scala-logging-slf4j" % scalaLogSLF4JV,
//  typesafeLoggingG %% "scala-logging" % scalaLogV
//)
//
//lazy val akkaHttp = akkaG %% "akka-http" % akkaHttpV
//lazy val akkaStream = akkaG %% "akka-stream" % akkaV
//
//lazy val excludedLoggers = Seq(
//  ExclusionRule(organization = typesafeLoggingG),
//  ExclusionRule(organization = slf4jG),
//  ExclusionRule(organization = logbackG)
//)
//
//lazy val ubirchConfig = ubirchUtilG %% "config" % "0.2.3" excludeAll (excludedLoggers: _*)
//lazy val ubirchDeepCheckModel = ubirchUtilG %% "deep-check-model" % "0.3.1" excludeAll (excludedLoggers: _*)
//lazy val ubirchResponse = ubirchUtilG %% "response-util" % "0.5.0" excludeAll (excludedLoggers: _*)
//
///*
// * RESOLVER
// ********************************************************/
//
////lazy val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")

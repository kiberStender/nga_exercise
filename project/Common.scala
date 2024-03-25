import sbt.{Def, *}
import Keys.*
import Dependencies.{io, *}

object Common {
  //val scalaV = "3.2.1"
  val scalaV = "2.13.13"
  lazy val settings: Seq[Def.Setting[? >: String & Task[Seq[String]] & Seq[String] & Boolean]] = Seq(
    organization := "nga_exercise",

    scalaVersion := scalaV,

    Global / scalacOptions                   := Seq("-P:kind-projector:underscore-placeholders"),
    Global / transitiveClassifiers           := Seq(Artifact.SourceClassifier),
    Test / parallelExecution                 := true
  )

  lazy val `domain-dependencies`: Seq[ModuleID] = Seq(
    org.typelevel.`cats-core`
  )

  lazy val `core-dependencies`: Seq[ModuleID] = Seq(
    // Test
    org.scalatest.scalatest,
    org.scalatestplus.`mockito-4-5`
  )

  lazy val `delivery-http4s-dependencies`: Seq[ModuleID] = Seq(
    com.github.pureconfig.`pureconfig-core`,
    io.circe.`circe-generic`,
    org.typelevel.`cats-effect`,
    org.typelevel.`log4cats-slf4j`,
    // Test
    org.scalatest.scalatest,
    org.scalatestplus.`mockito-4-5`,
    org.scalameta.munit
  )

  lazy val `persistence-skunk-dependencies`: Seq[ModuleID] = Seq(
    org.typelevel.`cats-core`,
    org.typelevel.`cats-effect`,
    // Test
    org.scalatest.scalatest
  )

  lazy val `main-http4s-cats-effect-dependencies`: Seq[ModuleID] = Seq(
    org.typelevel.`cats-effect`,
    org.typelevel.`log4cats-slf4j`,

    // Test
    org.slf4j.`slf4j-simple`
  )
}
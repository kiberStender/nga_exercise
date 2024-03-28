import sbt.{Def, *}
import Keys.*
import Dependencies.{io, *}

object Common {
  //val scalaV = "3.2.1"
  val scalaV = "2.13.13"
  lazy val settings: Seq[Def.Setting[? >: String & Task[Seq[String]] & Seq[String] & Boolean]] = Seq(
    organization := "nga_exercise",

    scalaVersion := scalaV,

    Global / scalacOptions                   := Seq("-P:kind-projector:underscore-placeholders", "-Ymacro-annotations"),
    Global / transitiveClassifiers           := Seq(Artifact.SourceClassifier),
    Test / parallelExecution                 := true
  )

  lazy val `domain-dependencies`: Seq[ModuleID] = Seq(
    com.github.`julien-truffaut`.`monocle-core`,
    com.github.`julien-truffaut`.`monocle-macro`,
    org.typelevel.`cats-core`,
    // Test
    org.scalatest.scalatest,
  )

  lazy val `core-dependencies`: Seq[ModuleID] = Seq(
    // Test
    org.scalatest.scalatest
  )

  lazy val `delivery-dependencies`: Seq[ModuleID] = Seq(
    org.typelevel.`cats-effect`,
    org.typelevel.`log4cats-slf4j`,
    co.fs.`fs2-io`,
    // Test
    org.scalatest.scalatest
  )

  lazy val `main-dependencies`: Seq[ModuleID] = Seq(
    org.typelevel.`cats-effect`,
    org.typelevel.`log4cats-slf4j`,

    // Test
    org.slf4j.`slf4j-simple`
  )
}
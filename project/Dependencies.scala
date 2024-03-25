import sbt._

object Dependencies {
  case object ch {
    case object qos {
      case object logback {
        val `logback-classic` = "ch.qos.logback" % "logback-classic" % "1.4.7"
      }
    }
  }

  case object co {
    case object fs {
      val `fs2-io` = "co.fs2" %% "fs2-io" % "3.10.0"
    }
  }

  case object com {
    case object github {
      case object `julien-truffaut` {
        val `monocle-core` = "com.github.julien-truffaut" %% "monocle-core" % "3.0.0-M6"
        val `monocle-macro` = "com.github.julien-truffaut" %% "monocle-macro" % "3.0.0-M6"
        val `monocle-law` = "com.github.julien-truffaut" %% "monocle-law" % "3.0.0-M6" % Test
      }

      case object pureconfig {
        val `pureconfig-core` = "com.github.pureconfig" %% "pureconfig-core" % "0.17.5"
      }
    }

    case object google {
      case object `api-client` {
        val `google-api-client` = "com.google.api-client" % "google-api-client" % "2.2.0"
      }
    }
  }

  case object io {
    case object circe {
      val `circe-generic` = "io.circe" %% "circe-generic" % "0.14.6"
      val `circe-parser` = "io.circe" %% "circe-parser" % "0.14.5"
    }

  }

  case object org {
    case object http4s {
      val `http4s-blaze-server` = "org.http4s" %% "http4s-blaze-server" % "0.23.16"
      val `http4s-circe` = "org.http4s" %% "http4s-circe" % "0.23.19"
      val `http4s-dsl` = "org.http4s" %% "http4s-dsl" % "0.23.25"
    }

    case object scalameta {
      val munit = "org.scalameta" %% "munit" % "0.7.29" % Test
    }

    case object scalatest {
      val scalatest = "org.scalatest" %% "scalatest" % "3.2.15" % Test
    }

    case object scalatestplus {
      val `mockito-4-5` = "org.scalatestplus" %% "mockito-4-5" % "3.2.12.0" % Test
    }

    case object slf4j {
      val `slf4j-api` = "org.slf4j" % "slf4j-api" % "2.0.9"
      val `slf4j-simple` = "org.slf4j" % "slf4j-simple" % "2.0.9"
    }

    case object typelevel {
      val `cats-core` = "org.typelevel" %% "cats-core" % "2.10.0"
      val `cats-effect` = "org.typelevel" %% "cats-effect" % "3.5.0"
      val `log4cats-core` = "org.typelevel" %% "log4cats-core" % "2.5.0"
      val `log4cats-slf4j` = "org.typelevel" %% "log4cats-slf4j" % "2.6.0"
    }

    case object tpolecat {
      val `skunk-core` = "org.tpolecat" %% "skunk-core" % "0.3.2"
      val `natchez-core` = "org.tpolecat" %% "natchez-core" % "0.3.5"
      val `natchez-log` = "org.tpolecat" %% "natchez-log" % "0.3.5"
    }
  }
}
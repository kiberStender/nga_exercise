ThisBuild / organization := "com.nga"
ThisBuild / scalaVersion := "3.1.3"
ThisBuild / version := "0.0.1-SNAPSHOT"

lazy val Cctt = "compile->compile;test->test"

lazy val root = (project in file("."))
  .settings(name := "nga_exercise")
  .aggregate(
    domain,
    core,
    delivery,
    main
  )

  lazy val domain = (project in file("01-domain"))
    .settings(Common.settings *)
    .settings(libraryDependencies ++= Common.`domain-dependencies`)
    .settings(addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.3" cross CrossVersion.full))
    .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))

  lazy val core =
    project
      .in(file("02-core"))
      .dependsOn(domain % Cctt)
      .settings(Common.settings *)
      .settings(libraryDependencies ++= Common.`core-dependencies`)
      .settings(addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.3" cross CrossVersion.full))
      .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))

  lazy val delivery =
    project
      .in(file("03-delivery"))
      .dependsOn(core % Cctt)
      .settings(Common.settings *)
      .settings(libraryDependencies ++= Common.`delivery-dependencies`)
      .settings(addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.3" cross CrossVersion.full))
      .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))


  lazy val main =
    project
      .in(file("04-main"))
      .dependsOn(delivery % Cctt)
      .settings(Common.settings *)
      .settings(libraryDependencies ++= Common.`main-dependencies`)
      .settings(addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.3" cross CrossVersion.full))
      .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))
      .enablePlugins(JavaAppPackaging)
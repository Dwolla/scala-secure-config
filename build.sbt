inThisBuild(List(
  organization := "com.dwolla",
  description := "Adds support for decrypting values in TypeSafe Config files",
  homepage := Some(url("https://github.com/Dwolla/scala-secure-config")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "bpholt",
      "Brian Holt",
      "bholt+github@dwolla.com",
      url("https://dwolla.com")
    ),
  ),
  crossScalaVersions := Seq("2.13.5", "2.12.13"),
  scalaVersion := crossScalaVersions.value.head,
  startYear := Option(2018),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),

  githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11"),
  githubWorkflowTargetTags ++= Seq("v*"),
  githubWorkflowPublishTargetBranches :=
    Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
  githubWorkflowPublish := Seq(
    WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    )
  ),
))

lazy val `secure-config` = (project in file("."))
  .settings(
    libraryDependencies ++= {
      Seq(
        "com.github.pureconfig" %% "pureconfig-cats-effect",
      ).map(_ % "0.14.1") ++
      Seq(
        "com.chuusai" %% "shapeless" % "2.3.3",
        "com.dwolla" %% "fs2-aws-java-sdk2" % "2.0.0-M11",
      )
    },
  )

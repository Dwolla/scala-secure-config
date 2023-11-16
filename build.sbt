ThisBuild / organization := "com.dwolla"
ThisBuild / description := "Adds support for decrypting values in TypeSafe Config files"
ThisBuild / homepage := Some(url("https://github.com/Dwolla/scala-secure-config"))
ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
ThisBuild / developers := List(
  Developer(
    "bpholt",
    "Brian Holt",
    "bholt+github@dwolla.com",
    url("https://dwolla.com")
  ),
)
ThisBuild / crossScalaVersions := Seq(
  "2.13.14",
  "3.3.3",
)
ThisBuild / scalaVersion := crossScalaVersions.value.head
ThisBuild / startYear := Option(2018)
ThisBuild / tlBaseVersion := "0.4"
ThisBuild / tlJdkRelease := Some(8)

lazy val `secure-config` = (project in file("."))
  .settings(
    libraryDependencies ++= {
      Seq(
        "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.4",
        "io.monix" %% "newtypes-core" % "0.3.0",
        "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sVersion.value,
        "com.disneystreaming.smithy4s" %% "smithy4s-aws-http4s" % smithy4sVersion.value,
        "org.typelevel" %% "mouse" % "1.3.1",
      )
    },
    smithy4sAwsSpecs ++= Seq(AWS.kms), // TODO can we put this into its own package to avoid clashing with generated code downstream?
    scalacOptions += "-Wconf:src=src_managed/.*:s",
  )
  .enablePlugins(
    Smithy4sCodegenPlugin,
  )

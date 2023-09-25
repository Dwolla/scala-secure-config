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
        "com.dwolla" %% "fs2-aws-java-sdk2" % "3.0.0-RC2",
      )
    },
  )

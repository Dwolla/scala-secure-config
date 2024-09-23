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

lazy val `smithy4s-preprocessors` = project
  .in(file("smithy4s-preprocessors"))
  .settings(
    scalaVersion := "2.12.13", // 2.12 to match what SBT uses
    scalacOptions -= "-source:future",
    libraryDependencies ++= {
      Seq(
        "org.typelevel" %% "cats-core" % "2.10.0",
        "software.amazon.smithy" % "smithy-build" % smithy4s.codegen.BuildInfo.smithyVersion,
      )
    },
  )
  .enablePlugins(NoPublishPlugin)

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
    smithy4sAwsSpecs ++= Seq(AWS.kms),
    scalacOptions += "-Wconf:src=src_managed/.*:s",
    Compile / smithy4sModelTransformers += "com.dwolla.smithy.ShadeNamespace",
    Compile / smithy4sAllDependenciesAsJars += (`smithy4s-preprocessors` / Compile / packageBin).value,
  )
  .enablePlugins(
    Smithy4sCodegenPlugin,
  )

import org.typelevel.sbt.gha.WorkflowStep.Sbt

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
  "2.13.16",
  "3.3.4",
)
ThisBuild / scalaVersion := crossScalaVersions.value.head
ThisBuild / startYear := Option(2018)
ThisBuild / tlBaseVersion := "0.4"
ThisBuild / tlJdkRelease := Some(8)
ThisBuild / githubWorkflowBuild := List(Sbt(List("compile", "test")))
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / mergifyRequiredJobs ++= Seq("validate-steward")
ThisBuild / mergifyStewardConfig ~= { _.map {
  _.withAuthor("dwolla-oss-scala-steward[bot]")
    .withMergeMinors(true)
}}

lazy val `smithy4s-preprocessors` = project
  .in(file("smithy4s-preprocessors"))
  .settings(
    scalaVersion := "2.12.20", // 2.12 to match what SBT uses
    scalacOptions -= "-source:future",
    libraryDependencies ++= {
      Seq(
        "org.typelevel" %% "cats-core" % "2.10.0",
        "software.amazon.smithy" % "smithy-build" % smithy4s.codegen.BuildInfo.smithyVersion,
      )
    },
    version := tlBaseVersion.value, // this module is unpublished and a stable version helps smithy4s not regenerate code more often than needed
  )
  .enablePlugins(NoPublishPlugin)

// TODO add tests for this
lazy val `scalafix-rules` = project.in(file("scalafix/rules"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion cross CrossVersion.for3Use2_13,
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "com.eed3si9n.expecty" %% "expecty" % "0.16.0" % Test,
    ),
    dependencyOverrides ++= Seq(
      "com.google.protobuf" % "protobuf-java" % "3.25.5", // CVE-2024-7254
    ),
    scalacOptions ~= {
      _.filterNot(_ == "-Xfatal-warnings")
    },
  )

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val `secure-config` = (project in file("."))
  .settings(
    libraryDependencies ++= {
      Seq(
        "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.9",
        "io.monix" %% "newtypes-core" % "0.3.0",
        "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sVersion.value,
        "com.disneystreaming.smithy4s" %% "smithy4s-aws-http4s" % smithy4sVersion.value,
        "org.scodec" %% "scodec-bits" % "1.2.4",
        "org.typelevel" %% "mouse" % "1.3.2",
        "org.scalameta" %% "munit" % "1.1.1" % Test,
        "org.http4s" %% "http4s-ember-client" % "0.23.30" % Test,
      )
    },
    libraryDependencies ++= {
      (scalaBinaryVersion.value) match {
        case "2.13" =>
          Seq("org.scala-lang" % "scala-compiler" % scalaVersion.value % Test)
        case _ =>
          Nil
      }
    },
    smithy4sAwsSpecs ++= Seq(AWS.kms),
    scalacOptions += "-Wconf:src=src_managed/.*:s",
    Compile / smithy4sModelTransformers += "com.dwolla.config.smithy.ShadeNamespace",
    Compile / smithy4sInternalDependenciesAsJars += (`smithy4s-preprocessors` / Compile / packageBin).value,
    Compile / smithy4sSmithyLibrary := false,
    Compile / scalafix / unmanagedSources := (Compile / sources).value,
    scalafixOnCompile := true,
    mimaBinaryIssueFilters ++= {
      import com.typesafe.tools.mima.core.*

      // exclusions in this package should be safe since everything in there should be package private
      val shadedPackage = "com.dwolla.config.smithy_shaded.com.amazonaws.kms"

      Seq(
        ProblemFilters.exclude[FinalMethodProblem](s"$shadedPackage.KMSOperation#Transformed.transform"),
        ProblemFilters.exclude[FinalMethodProblem](s"$shadedPackage.KMSOperation#reified.transform"),
      )
    }
  )
  .enablePlugins(
    Smithy4sCodegenPlugin,
  )
  .dependsOn(`scalafix-rules` % ScalafixConfig)

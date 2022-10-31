import xerial.sbt.Sonatype._

/* Main settings */

val organisation = "io.nlytx"
val projectName = "nlytx-nlp"
val projectVersion = "1.1.3"

lazy val commonSettings = Seq(
  scalaVersion := scalaLangV,
  organization := organisation
)

val apiName = "nlytx-nlp-api"
val apiVersion = projectVersion
val publish_api = true

val commonsName = "nlytx-nlp-commons"
val commonsVersion = projectVersion
val publish_commons = true

val expressionsName = "nlytx-nlp-expressions"
val expressionsVersion = projectVersion
val publish_expressions = true


/* Dependencies Versions */

val scalaLangV = "2.12.6"
val scalaParserV = "1.1.1"
val jblasV = "1.2.4"
val apacheComsCompressV = "1.17"
val apacheComsLangV = "3.7"
val akkaStreamV = "2.5.14"
val json4sV = "3.6.0"
val slf4jV = "1.7.25"
val logbackV = "1.2.3"
val scalatestV = "3.0.5"
val factorieNlpV = "1.0.4"
val factorieNlpModelsV = "1.0.3"

/* Projects */

lazy val nlytx_nlp = (project in file(".")).settings(
  name := apiName,
  version := apiVersion,
  commonSettings,
  libraryDependencies ++= (nlytxDeps ++ factorieDeps ++ commonDeps ++ testDeps),
  parallelExecution in Test := false,
  logBuffered in Test := false,
  publishApi
).aggregate(nlytx_nlp_commons,nlytx_nlp_expressions)

lazy val nlytx_nlp_commons = project.settings(
  name := commonsName,
  version := commonsVersion,
  commonSettings,
  libraryDependencies ++= testDeps,
  parallelExecution in Test := true,
  logBuffered in Test := false,
  publishCommons
)


lazy val nlytx_nlp_expressions = project.settings(
  name := expressionsName,
  version := expressionsVersion,
  commonSettings,
  libraryDependencies ++= (nlytxDeps ++ factorieDeps ++ commonDeps ++ testDeps),
  parallelExecution in Test := true,
  logBuffered in Test := false,
  publishCommons
).dependsOn(LocalProject("nlytx_nlp"))

/* Dependencies */

lazy val nlytxDeps = Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaStreamV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaStreamV,
)

lazy val factorieDeps = Seq(
  "io.nlytx" %% "factorie-nlp" % factorieNlpV,
  "io.nlytx" %% "factorie-nlp-models" % factorieNlpModelsV,
  "org.scala-lang.modules" %% "scala-parser-combinators" % scalaParserV,
  "org.scala-lang" % "scala-compiler" % scalaLangV,
  "org.scala-lang" % "scala-reflect" % scalaLangV,
  "org.jblas" % "jblas" % jblasV,
  "org.apache.commons" % "commons-compress" % apacheComsCompressV,
  "org.apache.commons" % "commons-lang3" % apacheComsLangV,
)

lazy val commonDeps = Seq(
	"org.json4s" %% "json4s-jackson" % json4sV,
	"org.slf4j" % "slf4j-api" % slf4jV,
	"ch.qos.logback" % "logback-classic" % logbackV
)	

lazy val testDeps = Seq(
  "org.scalactic" %% "scalactic" % scalatestV,
  "org.scalatest" %% "scalatest" % scalatestV % Test
)

/* Publishing  */

lazy val pubLicence = ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
lazy val pubOrg = Some("nlytx")
lazy val pubRepo = "nlytx-nlp"


lazy val publishCommons = {
  if (publish_commons) Seq(
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeProfileName := organisation,
    publishMavenStyle := true,
    licenses += pubLicence,
    sonatypeProjectHosting := Some(GitHubHosting("nlytx", "nlytx-nlp", "andrew@nlytx.io")),
    developers := List(
      Developer(
        id = "io.nlytx",
        name = "Andrew Gibson",
        email = "andrew@nlytx.io",
        url = url("http://nlytx.io")
      )
    ),
    //credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
//    name := organisation+":"+commonsName,
//    description := "General NLP tools",
//
//    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
//    organizationHomepage := Some(url("http://nlytx.io/")),
//    organization := organisation + "." + projectName,
//    organizationName := organisation,
//    scmInfo := Some(
//      ScmInfo(
//        url("https://github.com/nlytx/nlytx-nlp"),
//        "scm:git@github.com:nlytx/nlytx-nlp.git"
//      )
//    ),
//
      // Remove all additional repository other than Maven Central from POM
//      pomIncludeRepository := { _ => false },
//      publishTo := {
//        // For accounts created after Feb 2021:
//        val nexus = "https://s01.oss.sonatype.org/"
//        if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//        else Some("releases" at nexus + "service/local/staging/deploy/maven2")
//      },
//      publishMavenStyle := true

    //publishTo := sonatypePublishToBundle.value,
    //sonatypeCredentialHost := "s01.oss.sonatype.org"
  )
  else Seq(
    publishArtifact := false
  )
}

lazy val publishExpressions = {
  if (publish_expressions) Seq(
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeProfileName := organisation,
    publishMavenStyle := true,
    licenses += pubLicence,
    sonatypeProjectHosting := Some(GitHubHosting("nlytx", "nlytx-nlp", "andrew@nlytx.io")),
    developers := List(
      Developer(
        id = "io.nlytx",
        name = "Andrew Gibson",
        email = "andrew@nlytx.io",
        url = url("http://nlytx.io")
      )
    ),
    //credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
    //    description := "Identify reflective expressions",

//    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
//    organizationHomepage := Some(url("http://nlytx.io/")),
//    organization := organisation + "." + projectName,
//    organizationName := organisation,
//    scmInfo := Some(
//      ScmInfo(
//        url("https://github.com/nlytx/nlytx-nlp"),
//        "scm:git@github.com:nlytx/nlytx-nlp.git"
//      )
//    ),
//    developers := List(
//      Developer(
//        id = "io.nlytx",
//        name = "Andrew Gibson",
//        email = "andrew@nlytx.io",
//        url = url("http://nlytx.io")
//      )
//    ),
//    // Remove all additional repository other than Maven Central from POM
//    pomIncludeRepository := { _ => false },
//    publishTo := {
//      // For accounts created after Feb 2021:
//      val nexus = "https://s01.oss.sonatype.org/"
//      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
//    },
//    publishMavenStyle := true

    //publishTo := sonatypePublishToBundle.value,
    //sonatypeCredentialHost := "s01.oss.sonatype.org"
  )
  else Seq(
    publishArtifact := false
  )
}

lazy val publishApi = {
  if (publish_api) Seq(
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeProfileName := organisation,
    publishMavenStyle := true,
    licenses += pubLicence,
    sonatypeProjectHosting := Some(GitHubHosting("nlytx", "nlytx-nlp", "andrew@nlytx.io")),
    developers := List(
      Developer(
        id = "io.nlytx",
        name = "Andrew Gibson",
        email = "andrew@nlytx.io",
        url = url("http://nlytx.io")
      )
    ),
    //credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
    //    description := "Expose API for nlytx-nlp",

//    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"), //~/.sbt/sonatype_credentials
//    organizationHomepage := Some(url("http://nlytx.io/")),
//    organization := organisation + "." + projectName,
//    organizationName := organisation,
//    scmInfo := Some(
//      ScmInfo(
//        url("https://github.com/nlytx/nlytx-nlp"),
//        "scm:git@github.com:nlytx/nlytx-nlp.git"
//      )
//    ),
//    developers := List(
//      Developer(
//        id = "io.nlytx",
//        name = "Andrew Gibson",
//        email = "andrew@nlytx.io",
//        url = url("http://nlytx.io")
//      )
//    ),
//    // Remove all additional repository other than Maven Central from POM
//    pomIncludeRepository := { _ => false },
//    publishTo := {
//      // For accounts created after Feb 2021:
//      val nexus = "https://s01.oss.sonatype.org/"
//      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
//    },
//    publishMavenStyle := true

    //publishTo := sonatypePublishToBundle.value,
    //sonatypeCredentialHost := "s01.oss.sonatype.org"
  )
  else Seq(
    publishArtifact := false
  )
}


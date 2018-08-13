/* Main settings */

lazy val commonSettings = Seq(
  scalaVersion := "2.12.4",
  organization := "io.nlytx"
)

val projectName = "nlytx-nlp"

val apiName = "nlytx-nlp-api"
val apiVersion = "1.1.0"
val publish_api_to_BinTray = true

val commonsName = "nlytx-nlp-commons"
val commonsVersion = "1.0.0"
val publish_commons_to_BinTray = true

val expressionsName = "nlytx-nlp-expressions"
val expressionsVersion = "1.0.0"
val publish_expressions_to_BinTray = true


/* Dependencies Versions */

val scalaLangV = "2.12.4"
val scalaParserV = "1.0.6"
val jblasV = "1.2.4"
val apacheComsCompressV = "1.15"
val apacheComsLangV = "3.7"
val akkaStreamV = "2.5.6"
val json4sV = "3.5.3"
val slf4jV = "1.7.25"
val logbackV = "1.2.3"
val scalatestV = "3.0.4"
val factorieNlpV = "1.0.4"
val factorieNlpModelsV = "1.0.3"

/* Projects */

lazy val nlytx_nlp = (project in file(".")).settings(
  name := apiName,
  version := apiVersion,
  commonSettings,
  libraryDependencies ++= (nlytxDeps ++ factorieDeps ++ commonDeps ++ testDeps),
  resolvers += Resolver.bintrayRepo("nlytx","nlytx-nlp"),
  parallelExecution in Test := false,
  logBuffered in Test := false,
  publishApi
).aggregate(nlytx_nlp_commons,nlytx_nlp_expressions)

lazy val nlytx_nlp_commons = project.settings(
  name := commonsName,
  version := commonsVersion,
  commonSettings,
  libraryDependencies ++= testDeps,
  resolvers += Resolver.bintrayRepo("nlytx","nlytx-nlp"),
  parallelExecution in Test := true,
  logBuffered in Test := false,
  publishCommons
)


lazy val nlytx_nlp_expressions = project.settings(
  name := expressionsName,
  version := expressionsVersion,
  commonSettings,
  libraryDependencies ++= (nlytxDeps ++ factorieDeps ++ commonDeps ++ testDeps),
  resolvers += Resolver.bintrayRepo("nlytx","nlytx-nlp"),
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

lazy val binTrayRealm = "Bintray API Realm"
lazy val binTrayUrl = s"https://api.bintray.com/content/nlytx/nlytx-nlp/"
lazy val binTrayCred = Credentials(Path.userHome / ".bintray" / ".credentials")
lazy val pubLicence = ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

lazy val apiBinTray = Some(binTrayRealm at binTrayUrl + s"$apiName/$apiVersion/")
lazy val commonsBinTray = Some(binTrayRealm at binTrayUrl + s"$commonsName/$commonsVersion/")
lazy val expressionsBinTray = Some(binTrayRealm at binTrayUrl + s"$expressionsName/$expressionsVersion/")


lazy val publishCommons = {
  if (publish_commons_to_BinTray) Seq(
    publishMavenStyle := true,
    licenses += pubLicence,
    publishTo := commonsBinTray,
    credentials += binTrayCred
  )
  else Seq(
    publishArtifact := false
  )
}

lazy val publishExpressions = {
  if (publish_expressions_to_BinTray) Seq(
    publishMavenStyle := true,
    licenses += pubLicence,
    publishTo := expressionsBinTray,
    credentials += binTrayCred
  )
  else Seq(
    publishArtifact := false
  )
}

lazy val publishApi = {
  if (publish_api_to_BinTray) Seq(
    publishMavenStyle := true,
    licenses += pubLicence,
    publishTo := apiBinTray,
    credentials += binTrayCred
  )
  else Seq(
    publishArtifact := false
  )
}
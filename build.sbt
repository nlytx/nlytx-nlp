/* Main settings */

lazy val commonSettings = Seq(
  scalaVersion := "2.12.4",
  organization := "io.nlytx"
)

val apiName = "nlytx-nlp-api"
val factorieName = "factorie-nlp"
val modelsName = "factorie-nlp-models"

/* Versions */

val apiVersion = "1.0.0"
val factorieVersion = apiVersion
val modelsVersion = "1.0.2"

val scalaLangV = "2.12.4"
val scalaParserV = "1.0.6"
val jblasV = "1.2.4"
val apacheComsCompressV = "1.15"
val apacheComsLangV = "3.6"
val akkaStreamV = "2.5.6"
val json4sV = "3.5.3"
val slf4jV = "1.7.25"
val logbackV = "1.2.3"
val scalatestV = "3.0.4"

/* Projects */

lazy val nlytx_nlp_api = project.settings(
  name := apiName,
  version := apiVersion,
  commonSettings,
  libraryDependencies ++= (nlytxDeps ++ commonDeps ++ testDeps),
  publish := publishApi
).dependsOn(factorie_nlp)

lazy val factorie_nlp = project.settings(
	name:= factorieName,
  version := factorieVersion,
	commonSettings,
	libraryDependencies ++= (factorieDeps ++ commonDeps),
  publish := publishFactorie
).dependsOn(factorie_nlp_models)
	
lazy val factorie_nlp_models = project.settings(
  name := modelsName,
  version := modelsVersion,
  commonSettings,
  publishModels
)

/* Dependencies */

lazy val nlytxDeps = Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaStreamV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaStreamV,
)

lazy val factorieDeps = Seq(
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
  "org.scalatest" %% "scalatest" % scalatestV % Test
)

/* Publishing  */

lazy val binTrayUrl = s"https://api.bintray.com/content/nlytx/nlytx-nlp/"
lazy val binTrayCred = Credentials(Path.userHome / ".bintray" / ".credentials")
lazy val pubLicence = ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
lazy val modelsBinTray = Some("Bintray API Realm" at binTrayUrl + s"$modelsName/$modelsVersion/")
lazy val apiBinTray = Some("Bintray API Realm" at binTrayUrl + s"$apiName/$apiVersion/")
lazy val factorieBinTray = Some("Bintray API Realm" at binTrayUrl + s"$factorieName/$factorieVersion/")

lazy val publishModels = Seq(
  publishMavenStyle := true,
  licenses += pubLicence,
  publishTo := modelsBinTray,
  credentials += binTrayCred
)

lazy val publishApi = Seq(

)

lazy val publishFactorie = Seq(

)

/*

  pomExtra := (
    <distributionManagement>
      <repository>
        <id>bintray-nlytx-nlytx-nlp</id>
        <name>nlytx-nlytx-nlp</name>
        <url>https://api.bintray.com/maven/nlytx/nlytx-nlp/factorie-nlp-models/;publish=1</url>
      </repository>
    </distributionManagement>
    <profiles>
      <profile>
        <repositories>
          <repository>
            <snapshots>
              <enabled>false</enabled>
            </snapshots>
            <id>bintray-nlytx-nlytx-nlp</id>
            <name>bintray</name>
            <url>https://dl.bintray.com/nlytx/nlytx-nlp</url>
          </repository>
        </repositories>
        <pluginRepositories>
          <pluginRepository>
            <snapshots>
              <enabled>false</enabled>
            </snapshots>
            <id>bintray-nlytx-nlytx-nlp</id>
            <name>bintray-plugins</name>
            <url>https://dl.bintray.com/nlytx/nlytx-nlp</url>
          </pluginRepository>
        </pluginRepositories>
        <id>bintray</id>
      </profile>
    </profiles>
      <activeProfiles>
        <activeProfile>bintray</activeProfile>
      </activeProfiles>
    )
 */
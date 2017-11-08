[![Build Status](https://travis-ci.org/nlytx/nlytx-nlp.svg?branch=master)](https://travis-ci.org/nlytx/nlytx-nlp) ![scalaVersion](https://img.shields.io/badge/scala-2.12.4-red.svg) ![Liencse](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)

[ ![Download](https://api.bintray.com/packages/nlytx/nlytx-nlp/factorie-nlp-models/images/download.svg) ](https://bintray.com/nlytx/nlytx-nlp/factorie-nlp-models/_latestVersion) *factorie-nlp-models*

[ ![Download](https://api.bintray.com/packages/nlytx/nlytx-nlp/nlytx-nlp-api/images/download.svg) ](https://bintray.com/nlytx/nlytx-nlp/nlytx-nlp-api/_latestVersion) *nlytx-nlp-api*
# nlytx-nlp

A high level API for Factorie NLP services using akka streams.

### Quick Start

Add the following to your build.sbt file:

```sbtshell
libraryDependencies ++= Seq(
        "io.nlytx" %% "nlytx-nlp-api" % "1.0.2",
        "io.nlytx" %% "factorie-nlp" % "1.0.4",
        "io.nlytx" %% "factorie-nlp-models" % "1.0.3")

resolvers += Resolver.bintrayRepo("nlytx", "nlytx-nlp")
```

Access the API:

```scala
import io.nlytx.nlp.api.AnnotatorPipelines
import io.nlytx.nlp.api.DocumentModel.Document

val ap = AnnotatorPipelines

//The profile method is blocking and for testing only.
val doc:Document = ap.profile("This is a test document.")

//Check the doc has been tokenised
val success:Boolean = doc.tokenCount==6
```

### Documentation

Detailed docs pending

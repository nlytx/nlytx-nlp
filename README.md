[![Build Status](https://travis-ci.org/nlytx/nlytx-nlp.svg?branch=master)](https://travis-ci.org/nlytx/nlytx-nlp) ![scalaVersion](https://img.shields.io/badge/scala-2.12.4-red.svg) ![Liencse](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)

[ ![Download](https://api.bintray.com/packages/nlytx/nlytx-nlp/factorie-nlp-models/images/download.svg) ](https://bintray.com/nlytx/nlytx-nlp/factorie-nlp-models/_latestVersion) *factorie-nlp-models*

[ ![Download](https://api.bintray.com/packages/nlytx/nlytx-nlp/nlytx-nlp-api/images/download.svg) ](https://bintray.com/nlytx/nlytx-nlp/nlytx-nlp-api/_latestVersion) *nlytx-nlp-api*
# nlytx-nlp

This is a significantly modified version of [Factorie](https://github.com/factorie/factorie) providing only NLP services via a high level API using akka streams. For more detail on the differences [see below](#diffs)

### Quick Start

Add the following to your build.sbt file:

```scala
libraryDependencies ++= Seq(
        "io.nlytx" %% "nlytx-nlp-api" % "1.0.1",
        "io.nlytx" %% "factorie-nlp-models" % "1.0.3")

resolvers += Resolver.bintrayRepo("nlytx-io", "factorie-nlp-api")
```

Access the API:

```scala
import io.nlytx.nlp.api.AnnotatorPipelines
import io.nlytx.nlp.api.DocumentModel.Document

val ap = AnnotatorPipelines

//The profile method is blocking and for testing only.
val doc:Document = ap.profile("This is a test document.")

//Check the doc has been annotated
val success:Boolean = doc.tokenCount==6
```

### Documentation

Detailed docs pending

### Differences with Factorie<a name="diffs"></a>

It is opinionated and therefore does not include all of the NLP options contained in the original Factorie code base. In particular, the code for training new models and testing of models has been removed with the objective that this API be focused on delivering NLP services based on existing models. Therefore, it's anticipated that model training and testing might be provided via a separate API, or with the original Factorie code.

It is also not binary compatible with the original Factorie package as classes have been refactored to make it easier to distinguish the document model from the code that annotates text based on that model.

The package has also been stripped all of the command line code, code for accessing MongoDB, and the docs and examples associated with the general (non NLP) use of Factorie.

The original Factorie was a maven project with a custom sbt builder. This has been replaced it with a standard ```build.sbt```
and the libraries have been updated to allow it to run on Scala 2.12+.





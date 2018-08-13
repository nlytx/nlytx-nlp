[![Build Status](https://travis-ci.org/nlytx/nlytx-nlp.svg?branch=master)](https://travis-ci.org/nlytx/nlytx-nlp) ![scalaVersion](https://img.shields.io/badge/scala-2.12.6-red.svg) ![Liencse](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)

[ ![Download](https://api.bintray.com/packages/nlytx/nlytx-nlp/factorie-nlp-models/images/download.svg) ](https://bintray.com/nlytx/nlytx-nlp/factorie-nlp-models/_latestVersion) *factorie-nlp-models*

[ ![Download](https://api.bintray.com/packages/nlytx/nlytx-nlp/nlytx-nlp-api/images/download.svg) ](https://bintray.com/nlytx/nlytx-nlp/nlytx-nlp-api/_latestVersion) *nlytx-nlp-api*
# nlytx-nlp

A high level API for Factorie NLP services using akka streams.

### Quick Start

Add the following to your build.sbt file:

```sbtshell
libraryDependencies ++= Seq(
        "io.nlytx" %% "nlytx-nlp-api" % "1.1.2",
        "io.nlytx" %% "nlytx-nlp-commons" % "1.1.2",
        "io.nlytx" %% "nlytx-nlp-expressions" % "1.1.2",
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

Use TF/IDF in Commons:

```scala

//Docs to come soon

```

Use ReflectiveExpressions in Expressions:

```scala
package io.nlytx.expressions

import io.nlytx.expressions.ReflectiveExpressionPipeline
import scala.util.{Failure, Success}


object Application extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val REP = ReflectiveExpressionPipeline

  val text = """Firstly, this week was interesting and what I really liked about it was the picture game we played on monday. I was the one who had the picture and I found it hard to communicate exactly what my drawing was, which showed me just how important communication is if you want to be successful. It was a fun activity and it showed me some good first hand experience about how to communicate. Secondly, we got back our memo assignment and I did pretty decent on it but definitely need to rewrite it and work on my memo writing overall. I need to work on cutting out the fat and my passive writing style, which I know is going to be hard to change but I feel like with a lot of practice I will be able to vastly improve in this area. Lastly, we have started to gain a little progress on the team project and have come up with some ideas on how to go about researching our proposition. With regards to my action plan I can’t recall working on encouraging the heart within my group, so that is something I have to be more aware of when we are meeting and do my best to better myself in this area. I do feel like I have began to form some sort of a relationship with my group members, but I still need to improve these relationships in order for it to feel like I have really developed long term friendships/relationships within my group. This weekend my plan is to work on rewriting my memo and writing up my resume. I had a good time in class this week and I am looking forward to what next week has in store for us."""

  val result = REP.process(text)

  result.onComplete {
    case Success(res) => println(s"SUCCESS: $res")
    case Failure(err) => println(s"ERROR: $err")
  }

}
```

### Documentation

Detailed docs pending

package io.nlytx.nlp.api

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import io.nlytx.nlp.annotators._
import io.nlytx.nlp.api.DocumentModel.Document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */

object AnnotatorPipelines {

  implicit val system: ActorSystem = ActorSystem("factorie-nlp-api-as")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val logger = Logging(system.eventStream, "factorie-nlp-api")

  type Pipeline = String => RunnableGraph[Future[Document]]
  type DocPipeline = Document => RunnableGraph[Future[Document]]

  //Make Document
  private lazy val doc = Flow[String].map(new Document(_))

  //Document Annotators - no models required
  private lazy val tokeniser = (doc: Document) => Tokeniser.process(doc)
  private lazy val segmenter = (doc: Document) => Segmenter.process(doc)
  private lazy val normaliser = (doc: Document) => Normaliser.process(doc)

  //Document Annotators - load models

  private lazy val postagger = (doc: Document) => PosTagger.process(doc)
  private lazy val lemmatiser = (doc: Document) => Lemmatiser.process(doc)
  private lazy val parser = (doc: Document) => Parser.process(doc)

  //Very slow model loading - returns a future
  private lazy val nerTagger = (doc: Document) => Future(NerTagger.process(doc))


  //Pipelines in order of complexity

  val tokenPipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser))
      .toMat(Sink.head[Document])(Keep.right)

  val segmentPipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser).map(segmenter))
      .toMat(Sink.head[Document])(Keep.right)

  val postagPipeline:Pipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser).map(segmenter).map(normaliser).map(postagger))
      .toMat(Sink.head[Document])(Keep.right)

  //Segmenting, normalising and postagging only
  val fastPipeline:Pipeline = postagPipeline

  val lemmaPipeline:Pipeline = (s:String) =>
    Source.single(s)
      .via(doc.map(tokeniser).map(segmenter).map(normaliser).map(postagger).map(lemmatiser))
      .toMat(Sink.head[Document])(Keep.right)

  //Segmenting, normalising,  postagging, and lemmatising
  val defaultPipeline:Pipeline = lemmaPipeline

  val parseOnlyPipeline = (fd:Future[Document]) =>
    Source.fromFuture(fd)
      .map(parser)
      .toMat(Sink.head[Document])(Keep.right)

  val parserPipeline:Pipeline = (s:String) => parseOnlyPipeline(lemmaPipeline(s).run)

  private val nerOnlyPipeline = (fd:Future[Document]) =>
    Source.fromFuture(fd)
      .mapAsync(2)(nerTagger)
      .toMat(Sink.head[Document])(Keep.right)

  val completePipeline:Pipeline = (s:String) => nerOnlyPipeline(parseOnlyPipeline(lemmaPipeline(s).run).run)

  /* The main method for running a pipeline */
  def process(text:String,pipeline:Pipeline=defaultPipeline):Future[Document] = pipeline(text).run

  def processDoc(doc:Document,pipeline:DocPipeline):Future[Document] = pipeline(doc).run

  def profile(text:String,pipeline:Pipeline=defaultPipeline,wait:Int=180):Document = {
    logger.info(s"Profiling pipeline...")
    val start = System.currentTimeMillis()
    val doc = Await.result(process(text,pipeline), wait seconds)
    val time = System.currentTimeMillis() - start
    logger.info(s"Completed in ${time} ms")
    doc
  }

}

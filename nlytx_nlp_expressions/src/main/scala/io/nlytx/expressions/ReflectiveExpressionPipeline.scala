package io.nlytx.expressions

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import io.nlytx.expressions.analyser.PosTagAnalyser
import io.nlytx.expressions.data._
import io.nlytx.nlp.api.AnnotatorPipelines
import io.nlytx.nlp.api.DocumentModel.Document

import scala.concurrent.Future


object ReflectiveExpressionPipeline {

  implicit val system: ActorSystem = ActorSystem("reflective-expression-as")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  type Pipeline = String => RunnableGraph[Future[ReflectiveExpressions]]

  /* The main method for running the pipeline */
  def process(text:String):Future[ReflectiveExpressions] = refExPipeline(text).run

  val refExPipeline:Pipeline = (s:String) => {
    Source.single(s)
      .via(getDoc)
      .via(analysis)
      .toMat(Sink.head[ReflectiveExpressions])(Keep.right)
  }


  val getDoc: Flow[String, Document, NotUsed] = Flow[String].mapAsync[Document](2) { str =>
    val ap = AnnotatorPipelines
    ap.process(str, ap.postagPipeline)
  }

  val analysis: Flow[Document, ReflectiveExpressions, NotUsed] = Flow[Document].map { doc =>
    val codedSents = getCodedSents(doc)
    system.log.info("Coded sents: "+codedSents)
    ReflectiveExpressions(getReflect(doc), getSummary(codedSents), getCoded(codedSents))
  }

  def getReflect(doc: Document): Reflect = {
    val sents = doc.sentences.toVector
    val words = sents.flatMap(s => s.filterNot(_.isPunctuation).map(_.string))
    val wordLengths = words.map(_.length)
    val wc = words.length
    val awl = wordLengths.sum / wc.toDouble
    val sc = sents.length
    val asl = wc / sc.toDouble
    Reflect(wc, awl, sc, asl)
  }

  def getSummary(codedSents: Seq[CodedSentence]): Summary = {
    var metaTagSummary = codedSents.flatMap(_.metacognitionTags).groupBy(identity).mapValues(_.size)
    metaTagSummary += "none" -> codedSents.count(_.metacognitionTags.length < 1)
    Array("knowledge", "experience", "regulation").foreach { k =>
      if (!metaTagSummary.contains(k)) metaTagSummary += k -> 0
    }
    var phraseTagSummary = codedSents.flatMap(_.phraseTags).filterNot(_.contains("general")).groupBy(identity).mapValues(_.size)
    phraseTagSummary += "none" -> codedSents.count(_.phraseTags.length < 1)
    Array("outcome", "temporal", "pertains", "consider", "anticipate", "definite", "possible", "selfReflexive", "emotive", "selfPossessive", "compare", "manner").foreach { k =>
      if (!phraseTagSummary.contains(k)) phraseTagSummary += k -> 0
    }
    Summary(metaTagSummary, phraseTagSummary)
  }

  def getCodedSents(doc: Document): Seq[CodedSentence] = {
    val docSentences = doc.sentences.toSeq
    system.log.info(docSentences.toString)
    val sentencePosTags = docSentences.map(_.tokens.map(_.posTag.categoryValue))
    system.log.info(sentencePosTags.toString)
    PosTagAnalyser.analyse(sentencePosTags,docSentences.map(_.tokens.map(_.lemmaString)))
  }

  def getCoded(codedSentences: Seq[CodedSentence]): Seq[Coded] = {
    codedSentences.map { cs =>
      Coded(
        cs.sentence,
        cs.phrases.map(p => p.phrase + "[" + p.phraseType + "," + p.start + "," + p.end + "]"),
        //cs.phraseTags,
        cs.subTags,
        cs.metacognitionTags
      )
    }
  }
}

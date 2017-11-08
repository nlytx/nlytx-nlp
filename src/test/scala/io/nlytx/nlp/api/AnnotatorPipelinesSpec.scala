package io.nlytx.nlp.api

import io.nlytx.nlp.AsyncSequentialSpec
import org.scalatest._

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */

class AnnotatorPipelinesSpec extends AsyncSequentialSpec {

  private val ap = AnnotatorPipelines

  private val singleSentence = "A valid document has been created."
  private val twoSentences = singleSentence + " It should have two sentences."
  private val nerSentence = "This was written by Andrew from Australia."
  private val threeSentences = twoSentences + nerSentence


  "fastPipeline profile" should "result in a Document with postags" in {
    val doc = ap.profile(singleSentence,ap.fastPipeline,60)
    val tokens = doc.tokens.toVector
    val posString = tokens.map(_.posTag.value.toString).mkString(",")
    doc.tokenCount shouldBe 7
    doc.sentenceCount shouldBe 1
    doc.string shouldBe singleSentence
    posString shouldBe "DT,JJ,NN,VBZ,VBN,VBN,."
  }

  "defaultPipeline profile" should "result in a Document with postags and lemmas" in {
    val doc = ap.profile(twoSentences,ap.defaultPipeline,60)
    val tokens = doc.tokens.toVector
    val posString = tokens.map(_.posTag.value.toString).mkString(",")
    val lemmaString = tokens.map(_.lemma.value.toString).mkString(" ")
    doc.tokenCount shouldBe 13
    doc.sentenceCount shouldBe 2
    doc.string shouldBe twoSentences
    posString shouldBe "DT,JJ,NN,VBZ,VBN,VBN,.,PRP,MD,VB,CD,NNS,."
    lemmaString shouldBe "a valid document have be create . it should have two sentence ."
  }

  "defaultPipeline process" should "result in a Document with postags,lemmas (no log msg)" in {
    val futureDoc = ap.process(twoSentences,ap.defaultPipeline)
    futureDoc.map { doc =>
      val tokens = doc.tokens.toVector
      val posString = tokens.map(_.posTag.value.toString).mkString(",")
      val lemmaString = tokens.map(_.lemma.value.toString).mkString(" ")
      doc.tokenCount shouldBe 13
      doc.sentenceCount shouldBe 2
      doc.string shouldBe twoSentences
      posString shouldBe "DT,JJ,NN,VBZ,VBN,VBN,.,PRP,MD,VB,CD,NNS,."
      lemmaString shouldBe "a valid document have be create . it should have two sentence ."
    }
  }

  "parserPipeline process" should "result in a document with parsing" in {
    val futureDoc = ap.process(threeSentences,ap.parserPipeline)
    futureDoc.map { parserDoc =>
      val parseTree = parserDoc.sentences.head.parse.toString.replaceAllLiterally("\n",",")
      parserDoc.sentenceCount shouldBe 3
      parserDoc.string shouldBe threeSentences
      parseTree shouldBe "0 2 A/DT det,1 2 valid/JJ amod,2 5 document/NN nsubjpass,3 5 has/VBZ aux,4 5 been/VBN auxpass,5 -1 created/VBN root,6 5 ./. punct,"
    }
  }
/*
  "completePipeline" should "result in a document with nerTags" in {
    val futureDoc = ap.process(nerSentence,ap.completePipeline)
    futureDoc.map { completeDoc =>
      completeDoc.sentenceCount shouldBe 3
      completeDoc.string shouldBe threeSentences
      val nerTags = completeDoc.sentences.head.tokens.map(_.nerTag.baseCategoryValue).mkString(",")
      nerTags shouldBe "O,O,O,O,PER,O,LOC,O"
    }
  }
*/
}

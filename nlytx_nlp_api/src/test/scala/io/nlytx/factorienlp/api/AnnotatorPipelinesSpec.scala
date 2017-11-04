package io.nlytx.factorienlp.api

import io.nlytx.factorie_nlp.annotators.ModelLocator.StaticLexicons
import io.nlytx.factorie_nlp.api.AnnotatorPipelines
import org.scalatest._

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */

class AnnotatorPipelinesSpec extends FlatSpec with Matchers {

  private val ap = AnnotatorPipelines

  private val singleSentence = "A valid document has been created."
  private val twoSentences = singleSentence + " It should have two sentences."
  private val nerSentence = "This was written by Andrew from Australia."
  private val threeSentences = twoSentences + nerSentence


  "fastPipeline" should "result in a Document with postags" in {
    val doc = ap.profile(singleSentence,ap.fastPipeline,60)
    val tokens = doc.tokens.toVector
    val posString = tokens.map(_.posTag.value.toString).mkString(",")
    //val lemmaString = tokens.map(_.lemma.value.toString).mkString(" ")
    doc.tokenCount shouldBe 7
    doc.sentenceCount shouldBe 1
    doc.string shouldBe singleSentence
    posString shouldBe "DT,JJ,NN,VBZ,VBN,VBN,."
    //lemmaString shouldBe "a valid document has been created ."
  }
/*
  "defaultPipeline" should "result in a Document with postags and lemmas" in {
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

  "parserPipeline" should "result in a document with parsing" in {
    val parserDoc = ap.profile(threeSentences,ap.parserPipeline,120)
    val parseTree = parserDoc.sentences.head.parse.toString.replaceAllLiterally("\n",",")
    parserDoc.sentenceCount shouldBe 3
    parseTree shouldBe "0 2 A/DT det,1 2 valid/JJ amod,2 5 document/NN nsubjpass,3 5 has/VBZ aux,4 5 been/VBN auxpass,5 -1 created/VBN root,6 5 ./. punct,"
  }

  "StaticLexicons" should "provide access to lexicons" in {
    val sl = StaticLexicons
    sl.iesl.Continents.trie.size shouldBe 7
    sl.uscensus.PersonFirstMale.trie.size shouldBe 1219
    sl.wikipedia.Battle.trie.size shouldBe 9687
  }
*/
  /*
  "completePipeline" should "result in a document with nerTags" in {
    val completeDoc = ap.profile(nerSentence,ap.completePipeline,180)
    val nerTags = completeDoc.sentences.head.tokens.map(_.nerTag.baseCategoryValue).mkString(",")
    nerTags shouldBe "O,O,O,O,PER,O,LOC,O"
  }
 */

}

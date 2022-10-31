package io.nlytx.nlp.annotators

import io.nlytx.nlp.AsyncSequentialSpec
import io.nlytx.nlp.api.AnnotatorPipelines
import io.nlytx.nlp.api.DocumentModel.Document

/**
  * Created by andrew@andrewresearch.net on 5/11/17.
  */

class AnnotatorsSpec extends AsyncSequentialSpec {


  lazy val testText = "Today is £1 Tuesday. It is very hot, and I going to go swimming with Prof. Smith. We're thinking of going to Caloundra."
  lazy val doc = new Document(testText)

  /* Test Annotators in order so that they can add to the one document */

  behavior of "Tokeniser"
  it should "split a document into the correct number of tokens" in {
    doc.tokenCount shouldBe 0
    Tokeniser.process(doc)
    doc.tokenCount shouldBe 29
  }
  it should "normalise tokens" in {
    val normalised = doc.tokens.map(_.string).take(4).mkString(",")
    normalised shouldBe "Today,is,$,1"
  }

  "Segmenter" should "determine the correct number of sentences" in {
    doc.sentenceCount shouldBe 0
    Segmenter.process(doc)
    doc.sentenceCount shouldBe 3
  }

//  "Normaliser" should "" in {
//    //val startString = "Today,is,1£,Tuesday"
//    //val endString = "Today,is,1$,Tuesday_"
//    //doc.tokens.map(_.string).take(4).mkString(",") shouldBe startString
//    Normaliser.process(doc)
//    true shouldBe true
//  }

  "Postagger" should "Tag the verbs in a document" in {
    Option(doc.tokens.head.posTag) shouldBe None
    PosTagger.process(doc)
    doc.tokens.count(_.posTag.categoryValue.contains("VB")) shouldBe 8
  }

  // Lemmatiser uses Wordnet
  lazy val wn = Wordnet
  behavior of "Wordnet"
  it should "test antonyms correctly" in {
    wn.areAntonyms("good", "evil") shouldBe true
    wn.areAntonyms("good", "star") shouldBe false
    wn.areAntonyms("right", "left") shouldBe true
    wn.areAntonyms("right", "wrong") shouldBe true
    wn.areAntonyms("right", "evil") shouldBe false
    wn.areAntonyms(wn.lemma("goodness", "N"), "evil") shouldBe true
  }
  it should "test hypernyms correctly" in {
    wn.isHypernymOf("red", "crimson") shouldBe true
    wn.isHyponymOf("crimson", "crimson") shouldBe false
    wn.isHyponymOf("crimson", "red") shouldBe true
    wn.shareHypernyms("dog", "cat") shouldBe true
  }
  it should "test synonyms correctly" in {
    wn.areSynonyms("soul", "person") shouldBe true
    wn.areSynonyms("dog", "cat") shouldBe false
    wn.areSynonyms("hot", "cold") shouldBe false
    wn.areSynonyms(wn.lemma("goodness", "N"), "evil") shouldBe false
  }

  "Lemmatiser" should "produce lemmas from token strings" in {
    val select = (words:Vector[String]) => Vector(13,14,15,16).map(words(_))
    select(doc.tokens.map(_.lemmaString).toVector) shouldBe Vector("going","to","go","swimming")
    Lemmatiser.process(doc)
    select(doc.tokens.map(_.lemmaString).toVector) shouldBe Vector("go","to","go","swim")
  }

  behavior of "Parser"
  val futureDoc = AnnotatorPipelines.process(doc.string,AnnotatorPipelines.parserPipeline)
  it should "identify dependencies relationships" in {
    futureDoc.map { doc =>
      val indexes = doc.sentences.head.tokens.map(t => (t.positionInSentence,t.parseParentIndex))
      indexes shouldBe Vector((0,1), (1,-1), (2,3), (3,1), (4,1), (5,1))
    }
  }
  it should "label dependencies" in {
    futureDoc.map { doc =>
      val labels = doc.sentences.head.tokens.map(_.parseLabel.categoryValue).mkString(",")
      labels shouldBe "nsubj,root,nmod,attr,npadvmod,punct"
    }
  }

  // NerTagger requires lexicons
  "StaticLexicons" should "provide access to lexicons" in {
    lazy val sl = StaticLexicons
    sl.iesl.Continents.trie.size shouldBe 7
    sl.uscensus.PersonFirstMale.trie.size shouldBe 1219
    sl.wikipedia.Battle.trie.size shouldBe 9687
  }
  //TODO Find out how to test LexiconFeatures
  //  "EnglishLexiconFeatures" should "provide access to lexicons" in {
  //    lazy val elf = EnglishLexiconFeatures
  //    val document = new Document("Today is Tuesday.")
  //    val tokenSequence = document.tokens.toIndexedSeq
  //    elf.addLexiconFeatures(tokenSequence,(t: Token) => t.attr[NerTagger.ChainNERFeatures])
  //    elf.
  //  }

  //TODO Implement NerTagger test
//  "NerTagger" should "" in {
//    true shouldBe false
//  }

}


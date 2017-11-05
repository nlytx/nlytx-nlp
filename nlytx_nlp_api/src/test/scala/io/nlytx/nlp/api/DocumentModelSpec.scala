package io.nlytx.nlp.api

import cc.factorie.nlp.pos.PennPosTag
import io.nlytx.nlp.AsyncParallelSpec
import io.nlytx.nlp.api.{DocumentModel => nlytx}
import cc.factorie.{nlp => factorie}

/**
  * Created by andrew@andrewresearch.net on 5/11/17.
  */

class DocumentModelSpec extends AsyncParallelSpec {

  lazy val testDoc:nlytx.Document = new nlytx.Document("Test Document.")
  class TestSection extends nlytx.Section {
    override def document = ???
    override def stringStart = ???
    override def stringEnd = ???
  }

  val testSection:nlytx.Section = new TestSection

  "Document" should "be a factorie Document" in {
    testDoc shouldBe a [factorie.Document]
  }

  "Section" should "be a factorie Section" in {
    testSection shouldBe a [factorie.Section]
  }

  "Sentence" should "be a factorie Sentence" in {
    val sent:nlytx.Sentence = new nlytx.Sentence(testSection,0,1)
    sent shouldBe a [factorie.Sentence]
  }

  "Token" should "be a factorie Token" in {
    val token:nlytx.Token = new nlytx.Token(0,1)
    token shouldBe a [factorie.Token]
  }

  "PosTag" should "be a factorie PosTag" in {
    val postag:nlytx.PosTag = new nlytx.PosTag(new nlytx.Token(0,1),0)
    postag shouldBe a [factorie.pos.PosTag]
  }

  "NerTag" should "be a factorie NerTag" in {
    val nertag:nlytx.NerTag = new nlytx.NerTag(new nlytx.Token(0,1),"O")
    nertag shouldBe a [factorie.ner.NerTag]
  }




}

package io.nlytx.factorie_nlp.api

import cc.factorie.nlp
/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */
object DocumentModel {

  type Document = nlp.Document
  type Section = nlp.Section
  type Sentence = nlp.Sentence
  type Token = nlp.Token
  type PosTag = nlp.pos.PosTag
  type NerTag = nlp.ner.NerTag

}

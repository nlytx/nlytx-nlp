package io.nlytx.nlp.annotators

import io.nlytx.nlp.api.DocumentModel.Document

/**
  * Created by andrew@andrewresearch.net on 5/11/17.
  */

object Lemmatiser {
  def process(doc:Document) = Wordnet.wnLemmatizer.process(doc)
}
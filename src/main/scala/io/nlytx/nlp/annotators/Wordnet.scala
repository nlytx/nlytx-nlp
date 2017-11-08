package io.nlytx.nlp.annotators

import cc.factorie.nlp.wordnet

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */
object Wordnet extends wordnet.WordNet(ModelLocator.wordNetStreamFactory)
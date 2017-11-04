package io.nlytx.factorie_nlp.annotators

import cc.factorie.nlp.wordnet

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */
object WordNet extends wordnet.WordNet(ModelLocator.wordNetStreamFactory)
package io.nlytx.nlp.annotators

import cc.factorie.nlp.pos.OntonotesForwardPosTagger

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */

object PosTagger extends OntonotesForwardPosTagger(ModelLocator.locate[OntonotesForwardPosTagger]("/models/pos/")) with Serializable
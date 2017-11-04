package io.nlytx.factorie_nlp.annotators

import cc.factorie.nlp.parse.OntonotesTransitionBasedParser

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */

object Parser extends OntonotesTransitionBasedParser(ModelLocator.locate[OntonotesTransitionBasedParser]("/models/parse/")) with Serializable

package io.nlytx.nlp.annotators

import cc.factorie.nlp.lexicon.StaticLexicons

/**
  * Created by andrew@andrewresearch.net on 5/11/17.
  */
object StaticLexicons extends StaticLexicons()(ModelLocator.getLexiconProvider())
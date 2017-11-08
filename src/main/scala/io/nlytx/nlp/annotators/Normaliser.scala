package io.nlytx.nlp.annotators

import cc.factorie.nlp.Token
import cc.factorie.nlp.segment._
/**
  * Created by andrew@andrewresearch.net on 5/11/17.
  */

//Equivalent to PlainTokenNormalizer
object Normaliser extends TokenNormalizer1((t:Token, s:String) => new PlainNormalizedTokenString(t,s))

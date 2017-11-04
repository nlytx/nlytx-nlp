package cc.factorie.nlp.segment

import cc.factorie.nlp.Token

object PlainTokenNormalizer extends TokenNormalizer1((t:Token, s:String) => new PlainNormalizedTokenString(t,s))
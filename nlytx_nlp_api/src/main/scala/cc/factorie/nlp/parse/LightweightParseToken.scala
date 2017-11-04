package cc.factorie.nlp.parse

import cc.factorie.nlp.Token
import cc.factorie.nlp.pos.PosTag

class LightweightParseToken(t: Token){
  lazy val string = t.string
  lazy val posTag = t.attr[PosTag]
  lazy val lemma = if(posTag ne null) t.lemmaString else string
  lazy val lemmaLower = if(posTag ne null) lemma.toLowerCase else string
  lazy val posTagString = if(posTag ne null) posTag.categoryValue else string
}

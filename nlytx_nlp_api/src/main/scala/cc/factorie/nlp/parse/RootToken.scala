package cc.factorie.nlp.parse

import cc.factorie.nlp.Token

object RootToken extends LightweightParseToken(null.asInstanceOf[Token]){
  override lazy val string = ParserConstants.ROOT_STRING
  override lazy val lemmaLower = ParserConstants.ROOT_STRING
  override lazy val posTagString = ParserConstants.ROOT_STRING
}
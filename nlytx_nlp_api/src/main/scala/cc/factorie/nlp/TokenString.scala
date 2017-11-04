package cc.factorie.nlp

import cc.factorie.variable.StringVariable

/** Used as an attribute of Token when the token.string should return something
  * different than the document.string.substring at the Token's start and end positions.
  * For example, de-hyphenation may change "probab\n-ly" to "probably". */
class TokenString(val token:Token, s:String) extends StringVariable(s)


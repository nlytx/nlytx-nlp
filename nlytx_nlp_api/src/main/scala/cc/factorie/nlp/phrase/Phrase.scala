package cc.factorie.nlp.phrase

import cc.factorie.nlp.{Section, Token, TokenSpan}
import cc.factorie.nlp.parse.ParseTreeLabelDomain
import cc.factorie.nlp.pos.PennPosDomain
import cc.factorie.util.Attr


/** A Phrase is a TokenSpan that has a head token.
  * If offsetToHeadToken is unspecified, then it will be set automatically using HeadTokenOffset.apply. */
class Phrase(section:Section, start:Int, length:Int, offsetToHeadToken: Int) extends TokenSpan(section, start, length) with Attr {
  def this(span:TokenSpan, headTokenIndex:Int = -1) = this(span.section, span.start, span.length, headTokenIndex)

  assert(offsetToHeadToken == -1 || offsetToHeadToken >= 0 && offsetToHeadToken < length, "Offset from beginning of span, headTokenOffset="+offsetToHeadToken+", but span only has length "+length)
  lazy val headTokenOffset = if (offsetToHeadToken == -1) HeadTokenOffset(this) else offsetToHeadToken

  def headToken: Token = this.apply(headTokenOffset)

  def isPronoun = { val i = headToken.posTag.intValue; i == PennPosDomain.prpIndex || i == PennPosDomain.prpdIndex || i == PennPosDomain.wpIndex || i == PennPosDomain.wpdIndex }
  def isProperNoun = { val i = headToken.posTag.intValue; i == PennPosDomain.nnpIndex || i == PennPosDomain.nnpsIndex }
  def isNoun = headToken.posTag.categoryValue(0) == 'N'
  def isPossessive = headToken.posTag.intValue == PennPosDomain.posIndex
  def isAppositionOf(other:Phrase) : Boolean = (headToken.parseLabel.intValue == ParseTreeLabelDomain.appos) && (headToken.parseParent == other.headToken)

  def gender = this.attr[Gender]
  def number = this.attr[Number]
  def nounPhraseType = this.attr[NounPhraseType]

}

package cc.factorie.nlp.pos

import cc.factorie.nlp.Token

/** A categorical variable, associated with a token, holding its Penn Treebank part-of-speech category.  */
class PennPosTag(token:Token, initialIndex:Int)
  extends PosTag(token, initialIndex) with Serializable {
  def this(token:Token, initialCategory:String) = this(token, PennPosDomain.index(initialCategory))
  final def domain = PennPosDomain
  def isNoun = PennPosDomain.isNoun(categoryValue)
  def isProperNoun = PennPosDomain.isProperNoun(categoryValue)
  def isVerb = PennPosDomain.isVerb(categoryValue)
  def isAdjective = PennPosDomain.isAdjective(categoryValue)
  def isPersonalPronoun = PennPosDomain.isPersonalPronoun(categoryValue)
}


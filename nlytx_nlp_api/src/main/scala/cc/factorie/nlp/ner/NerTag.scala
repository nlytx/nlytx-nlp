package cc.factorie.nlp.ner

import cc.factorie.nlp.Token
import cc.factorie.variable.CategoricalVariable

// A "Tag" is a categorical label associated with a token.

/** An abstract class for a variable holding the part-of-speech tag of a Token.
     More specific subclasses have a domain, such as BilouConllNerDomain.
     @author Andrew McCallum */
abstract class NerTag(val token:Token, initialCategory:String) extends CategoricalVariable(initialCategory) {
  /** Return "PER" instead of "I-PER". */
  def baseCategoryValue: String = if (categoryValue.length > 1 && categoryValue(1) == '-') categoryValue.substring(2) else categoryValue

  def isEmpty = categoryValue == "O" // this should always be correct, but it might not be
  def spanPrefix = categoryValue.split("-").apply(0)
}
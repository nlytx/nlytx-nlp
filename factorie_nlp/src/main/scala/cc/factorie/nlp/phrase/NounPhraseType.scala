package cc.factorie.nlp.phrase

import cc.factorie.variable.CategoricalVariable


/** Categorical variable indicating whether the noun phrase is a pronoun, common noun phrase or proper noun phrase.
  * (In earlier versions this was called "MentionType", but it really is an attribute of the Phrase.)
  *
  * @author Andrew McCallum */
class NounPhraseType(val phrase:Phrase, targetValue:String) extends CategoricalVariable(targetValue) {
  def domain = NounPhraseTypeDomain
}

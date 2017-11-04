package cc.factorie.nlp.phrase

import cc.factorie.variable.CategoricalDomain

/** Categorical domain indicating whether the noun phrase is a pronoun, common noun phrase or proper noun phrase.
  *
  * @author Andrew McCallum */
object NounPhraseTypeDomain extends CategoricalDomain(List("PRO", "NOM", "NAM")) // TODO consider renaming these to "PRONOUN", "COMMON", "PROPER". -akm

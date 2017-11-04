package cc.factorie.nlp.ner

import cc.factorie.nlp.TokenSpan
import cc.factorie.variable.CategoricalVariable

/** A categorical variable holding the named entity type of a TokenSpan.
  * More specific subclasses have a domain, such as ConllNerDomain.
  *
  * @author Andrew McCallum */
abstract class NerSpanLabel(val span:TokenSpan, initialCategory:String) extends CategoricalVariable(initialCategory)

package cc.factorie.nlp.ner

import cc.factorie.nlp.TokenSpan

class ConllNerSpanLabel(span:TokenSpan, initialCategory:String) extends NerSpanLabel(span, initialCategory) with Serializable { def domain = ConllNerDomain }


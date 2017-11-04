package cc.factorie.nlp.ner

import cc.factorie.nlp.Section

class ConllNerSpan(section:Section, start:Int, length:Int, category:String) extends NerSpan(section, start, length) with Serializable { val label = new ConllNerSpanLabel(this, category) }

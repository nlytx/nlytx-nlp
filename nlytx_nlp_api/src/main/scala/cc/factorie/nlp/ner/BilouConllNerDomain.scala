package cc.factorie.nlp.ner

import cc.factorie.nlp.Section
import cc.factorie.variable.CategoricalDomain

object BilouConllNerDomain extends CategoricalDomain[String] with BILOU with BilouNerDomain {
  this ++= encodedTags(ConllNerDomain.categories)
  freeze()
  def spanList(section:Section): ConllNerSpanBuffer = {
    val boundaries = bilouBoundaries(section.tokens.map(_.attr[BilouConllNerTag].categoryValue))
    new ConllNerSpanBuffer ++= boundaries.map(b => new ConllNerSpan(section, b._1, b._2, b._3))
  }
}

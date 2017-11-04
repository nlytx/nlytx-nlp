package cc.factorie.nlp.ner

import cc.factorie.nlp.Token

class BilouConllNerTag(token:Token, initialCategory:String) extends NerTag(token, initialCategory) with Serializable {
  def domain = BilouConllNerDomain
}

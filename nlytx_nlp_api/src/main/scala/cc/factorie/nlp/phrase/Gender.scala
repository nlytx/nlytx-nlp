package cc.factorie.nlp.phrase

import cc.factorie.variable.CategoricalVariable

class Gender(categoryIndex:Int) extends CategoricalVariable[String](categoryIndex) {
  def this(category:String) = this(GenderDomain.index(category))
  final def domain = GenderDomain
}

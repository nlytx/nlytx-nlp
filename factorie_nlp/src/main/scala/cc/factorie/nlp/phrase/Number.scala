package cc.factorie.nlp.phrase

import cc.factorie.variable.CategoricalVariable

class Number extends CategoricalVariable[String] {
  def this(value:String) = { this(); _initialize(domain.index(value)) }
  def this(value:Int) = { this(); _initialize(value) }
  def domain = NumberDomain
}

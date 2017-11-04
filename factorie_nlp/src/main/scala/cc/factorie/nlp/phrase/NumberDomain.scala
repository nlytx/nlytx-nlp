package cc.factorie.nlp.phrase

import cc.factorie.variable.EnumDomain

object NumberDomain extends EnumDomain {
  val UNKNOWN,     // uncertain
  SINGULAR,        // one of something
  PLURAL = Value   // multiple of something
  freeze()
}

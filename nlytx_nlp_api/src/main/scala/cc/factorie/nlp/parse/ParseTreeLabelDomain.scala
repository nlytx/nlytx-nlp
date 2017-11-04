package cc.factorie.nlp.parse

import cc.factorie.variable.EnumDomain

// TODO I think this should instead be "ParseEdgeLabelDomain". -akm
object ParseTreeLabelDomain extends EnumDomain {
  val acomp, advcl, advmod, agent, amod, appos, attr, aux, auxpass, cc, ccomp, complm, conj, csubj, csubjpass,
  dep, det, dobj, expl, hmod, hyph, infmod, intj, iobj, mark, meta, neg, nmod, nn, npadvmod, nsubj, nsubjpass,
  num, number, oprd, parataxis, partmod, pcomp, pobj, poss, possessive, preconj, predet, prep, prt, punct,
  quantmod, rcmod, root, xcomp = Value
  index("") // necessary for empty categories
  freeze()
  def defaultCategory = "nn"
  def defaultIndex = index(defaultCategory)
}
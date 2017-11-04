package cc.factorie.nlp.parse

import cc.factorie.variable.LabeledCategoricalVariable


// TODO I think this should instead be "ParseEdgeLabels extends LabeledCategoricalSeqVariable". -akm
class ParseTreeLabel(val tree:ParseTree, targetValue:String = ParseTreeLabelDomain.defaultCategory) extends LabeledCategoricalVariable(targetValue) { def domain = ParseTreeLabelDomain }

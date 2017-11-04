package cc.factorie.nlp.pos

import cc.factorie.nlp.Token
import cc.factorie.variable.CategoricalLabeling



/** A categorical variable, associated with a token, holding its Penn Treebank part-of-speech category,
  * which also separately holds its desired correct "target" value.  */
class LabeledPennPosTag(token:Token, targetValue:String) extends PennPosTag(token, targetValue) with CategoricalLabeling[String] with Serializable
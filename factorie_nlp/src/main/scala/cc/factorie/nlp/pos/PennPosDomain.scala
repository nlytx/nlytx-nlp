package cc.factorie.nlp.pos

import cc.factorie.variable.CategoricalDomain

/** Penn Treebank part-of-speech tag domain. */
object PennPosDomain extends CategoricalDomain[String] {
  this ++= Vector(
    "#", // In WSJ but not in Ontonotes
    "$",
    "''",
    ",",
    "-LRB-",
    "-RRB-",
    ".",
    ":",
    "CC",
    "CD",
    "DT",
    "EX",
    "FW",
    "IN",
    "JJ",
    "JJR",
    "JJS",
    "LS",
    "MD",
    "NN",
    "NNP",
    "NNPS",
    "NNS",
    "PDT",
    "POS",
    "PRP",
    "PRP$",
    "PUNC",
    "RB",
    "RBR",
    "RBS",
    "RP",
    "SYM",
    "TO",
    "UH",
    "VB",
    "VBD",
    "VBG",
    "VBN",
    "VBP",
    "VBZ",
    "WDT",
    "WP",
    "WP$",
    "WRB",
    "``",
    "ADD", // in Ontonotes, but not WSJ
    "AFX", // in Ontonotes, but not WSJ
    "HYPH", // in Ontonotes, but not WSJ
    "NFP", // in Ontonotes, but not WSJ
    "XX" // in Ontonotes, but not WSJ
  )
  freeze()
  // Short-cuts for a few commonly-queried tags
  val posIndex = index("POS")
  val nnpIndex = index("NNP")
  val nnpsIndex = index("NNPS")
  val prpIndex = index("PRP")
  val prpdIndex = index("PRP$")
  val wpIndex = index("WP")
  val wpdIndex = index("WP$")
  val ccIndex = index("CC")

  def isNoun(pos:String): Boolean = pos(0) == 'N'
  def isProperNoun(pos:String) = { pos == "NNP" || pos == "NNPS" }
  def isVerb(pos:String) = pos(0) == 'V'
  def isAdjective(pos:String) = pos(0) == 'J'
  def isPersonalPronoun(pos: String) = pos == "PRP"
}
package cc.factorie.nlp.parse

case class ParseDecision(action: String) {
  val Array(lrnS, srpS, label) = action.split(" ")
  val leftOrRightOrNo = lrnS.toInt 		// leftarc-rightarc-noarc
  val shiftOrReduceOrPass = srpS.toInt	// shift-reduce-pass
  override def toString = action
  def readableString = s"${ParserConstants(leftOrRightOrNo)} ${ParserConstants(shiftOrReduceOrPass)} $label"
}

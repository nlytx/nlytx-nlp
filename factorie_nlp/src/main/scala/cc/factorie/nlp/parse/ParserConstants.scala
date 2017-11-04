package cc.factorie.nlp.parse

object ParserConstants {
  val NOTHING = -1

  val ROOT_ID = 0

  val SHIFT  = 1
  val REDUCE = 2
  val PASS   = 3

  val LEFT  = 4
  val RIGHT = 5
  val NO    = 6

  val TRAINING   = 7
  val PREDICTING = 8
  val BOOSTING   = 9
  val PREDICTING_FAST = 10

  val NULL_STRING = "<NULL>"
  val ROOT_STRING = "<ROOT>"
  val SEP = "|"

  // for debugging purposes
  def apply(i: Int): String = i match {
    case NOTHING => "nothing"

    case SHIFT => "shift"
    case REDUCE => "reduce"
    case PASS => "pass"

    case LEFT => "left"
    case RIGHT => "right"
    case NO => "no"

    case TRAINING => "training"
    case PREDICTING => "predicting"
    case BOOSTING => "boosting"

    case ROOT_ID => "root id"

    case _ => throw new Error(s"Integer value $i is not defined in ParserConstants")
  }
}

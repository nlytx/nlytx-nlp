package cc.factorie.nlp.ner

import cc.factorie.variable.CategoricalDomain

/** BILOU span encoding (Beginning, Inside, Last, Outside, Unit) */
trait BILOU extends SpanEncoding {
  this : CategoricalDomain[String] =>
  def prefixes = Set("B-", "I-", "L-", "U-")
  def isLicitTransition(from: String, to: String) = BILOU.licitTransitions contains from -> to

  def splitNerTag(tag:String):(String, Option[String]) = if(tag == "O") "O" -> None else {
    val Array(pre, cat) = tag.split("-")
    if(pre == "U") {
      pre -> None
    } else {
      pre -> Some(cat)
    }
  }

  def isLicit(from: this.type#Value, to: this.type#Value) =
    splitNerTag(from.category) -> splitNerTag(to.category) match {
      case ((fromPre, Some(fromCat)), (toPre, Some(toCat))) => toCat == fromCat && BILOU.licitTransitions.contains(fromPre -> toPre)
      case ((fromPre, _), (toPre, _)) => BILOU.licitTransitions contains fromPre -> toPre
    }

}

object BILOU {
  val licitTransitions = Set(
    "O" -> "B",
    "O" -> "U",
    "O" -> "O",

    "B" -> "I",
    "B" -> "L",

    "I" -> "I",
    "I" -> "L",

    "L" -> "O",
    "L" -> "B",
    "L" -> "U",

    "U" -> "U",
    "U" -> "B",
    "U" -> "O"
  )
}
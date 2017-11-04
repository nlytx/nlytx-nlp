package cc.factorie.nlp.lexicon

import scala.collection.mutable.HashMap

class SuffixNode {
  var endState: Boolean = false
  val contents = new HashMap[String, SuffixNode]
  def get(s: String): SuffixNode = { contents.getOrElse(s, null) }
  def put(s: String, n: SuffixNode): Unit = { contents.put(s, n) }
  def setEndState(b: Boolean): Unit = {endState = b}
  def isEndState: Boolean = endState
  def contains(s: String): Boolean = contents.contains(s)
  override def toString(): String = {
    var st = ""
    contents.keys.foreach(k => {
      st += s"[ $k ] --> ${contents(k).toString()} \n"
    })
    st
  }
}

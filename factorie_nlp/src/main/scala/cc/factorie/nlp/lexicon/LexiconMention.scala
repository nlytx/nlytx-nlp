package cc.factorie.nlp.lexicon

import java.io.Serializable

/**
  * An Aho-Corasick mention, containing the mention string, and the start & end
  * character indices in the original text.
  */
class LexiconMention(val mention : String, val startIdx : Int, val endIdx : Int) extends Serializable {
  override def toString() : String = { "Mention: " + mention + ", startIdx = " + startIdx + ", endIdx = " + endIdx }

  override def hashCode() : Int = { mention.hashCode() ^ startIdx ^ endIdx }

  override def equals(obj : Any) : Boolean = {
    if (obj == null) {
      return false
    }
    if (getClass() != obj.getClass()) {
      return false
    }
    val other = obj.asInstanceOf[LexiconMention]
    if (!this.mention.equals(other.mention)) {
      return false
    }
    if (this.startIdx != other.startIdx) {
      return false
    }
    if (this.endIdx != other.endIdx) {
      return false
    }
    return true
  }
}

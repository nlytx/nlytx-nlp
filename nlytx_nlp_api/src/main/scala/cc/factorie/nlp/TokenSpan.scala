package cc.factorie.nlp

import cc.factorie.util.Attr
import cc.factorie.variable.SpanVariable

import scala.collection.mutable


/** A sub-sequence of Tokens within a Section (which is in turn part of a Document). */
class TokenSpan(theSection:Section, initialStart:Int, initialLength:Int)
  extends SpanVariable[Section,Token](theSection, initialStart, initialLength) with Attr with Ordered[TokenSpan] with Serializable {
  def this(tokens:Seq[Token]) = this(tokens.head.section, tokens.head.positionInSection, tokens.size)
  /** The Document Section of which this TokenSpan is a subsequence. */
  final def section = chain  // Just a convenient alias
  /** The Document to which this TokenSpan belongs. */
  final def document = chain.document
  /** The indexed sequence of tokens contained in this TokenSpan. */
  final def tokens = this.toIndexedSeq //value
  /** The Sentence to which the first Token in this TokenSpan belongs. */
  def sentence = tokens(0).sentence
  // TODO Implement something like this? def containsSentenceIndex(i:Int): Boolean // Does this TokenSpan contain the token in the ith position of the sentence containing this TokenSpan.

  /** Return the substring of the Document covered by this TokenSpan.
      If this is a multi-Token TokenSpan, this will include all original characters in the Document, including those skipped by tokenization. */
  def documentString: String = document.string.substring(tokens.head.stringStart, tokens.last.stringEnd) // TODO Handle Token.attr[TokenString] changes
  /** Return a String representation of this TokenSpan, concatenating each Token.string, separated by the given separator. */
  def tokensString(separator:String): String = if (length == 1) tokens(0).string else tokens.map(_.string).mkString(separator)
  /** Return a String representation of this TokenSpan, concatenating each Token.string, separated by a space.
      This nicely avoids newlines, HTML or other junk that might be in the phrase.documentString. */
  def string: String = tokensString(" ")
  /** Returns true if this span contain the words of argument span in order. */
  def containsStrings(span:TokenSpan): Boolean = {
    for (i <- 0 until length) {
      if (length - i < span.length) return false
      var result = true
      var i2 = i; var j = 0
      while (j < span.length && i2 < this.length && result) {
        if (span.tokens(j).string != tokens(i2).string) result = false
        j += 1; i2 += 1
      }
      if (result) return true
    }
    false
  }
  override def toString = "TokenSpan("+start+","+end+":"+this.string+")"

  /**
    * Returns the character offsets of this TokenSpan into the raw text of its original document.
    */
  def characterOffsets:(Int, Int) = this.head.stringStart -> this.last.stringEnd

  /**
    * Returns a sequence of tokens that contains @param size tokens before and after the tokenspan.
    */
  def contextWindow(size:Int):Seq[Token] = {
    var idx = 0
    var window = mutable.ArrayBuffer[Token]()
    var t = Option(this.head)
    while(idx < size && t.isDefined) {
      t = t.flatMap(_.getPrev)
      window ++= t
      idx += 1
    }
    window = window.reverse // because we want things to be in their proper order, but do we want it this much?
    idx = 0
    t = Option(this.last)
    while(idx < size && t.isDefined) {
      t = t.flatMap(_.getNext)
      window ++= t
      idx += 1
    }
    window
  }

  /**
    * Returns an iterable over tokens before and after the token span without preserving order
    */
  def contextBag(size:Int):Iterable[Token] = {
    var idx = 0
    var window = mutable.ArrayBuffer[Token]()
    var t = Option(this.head)
    while(idx < size && t.isDefined) {
      t = t.flatMap(_.getPrev)
      window ++= t
      idx += 1
    }
    idx = 0
    t = Option(this.last)
    while(idx < size && t.isDefined) {
      t = t.flatMap(_.getNext)
      window ++= t
      idx += 1
    }
    window
  }


  /**
    * Implements ordering between two tokenspans, assumed to share the same document
    */
  def compare(other: TokenSpan): Int = if(this.section.head.stringStart > other.section.head.stringStart) {
    1
  } else if(this.section.head.stringStart < other.section.head.stringStart) {
    -1
  } else {
    if (this.sentence.start > other.sentence.start) {
      1
    } else if (this.sentence.start < other.sentence.start) {
      -1
    } else {
      if (this.start > other.start) {
        1
      } else if (this.start < other.start) {
        -1
      } else {
        if (this.end < other.end) {
          1
        } else if (this.end > other.end) {
          -1
        } else {
          0
        }
      }
    }
  }
}
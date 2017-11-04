package cc.factorie.nlp

/** A portion of the string contents of a Document.
 *
    *@author Andrew McCallum */
trait DocumentSubstring {
  /** The Document of which this DocumentSubstring is a part. */
  def document: Document
  /** The character offset into the Document.string at which this DocumentSubstring begins. */
  def stringStart: Int
  /** The character offset into the Document.string at which this DocumentSubstring is over.
      In other words, the last character of the DocumentSubstring is Document.string(this.stringEnd-1). */
  def stringEnd: Int
  /** The substring of the Document encompassed by this DocumentSubstring. */
  def string: String
}
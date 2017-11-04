package cc.factorie.nlp

/** Used as an attribute on Document to hold the document's name. */
case class DocumentName(string:String) {
  override def toString: String = string
}

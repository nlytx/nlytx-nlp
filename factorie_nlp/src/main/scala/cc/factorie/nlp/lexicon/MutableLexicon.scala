package cc.factorie.nlp.lexicon

import java.io.{File, InputStream}

import scala.io.{Codec, Source}

trait MutableLexicon extends Lexicon {
  // For populating the lexicon
  /** Tokenize and lemmatize the input String and add it as a single entry to the Lexicon */
  def +=(phrase:String): Unit
  /** All a lines from the input Source to this lexicon.  Source is assumed to contain multiple newline-separated lexicon entries */
  def ++=(source:Source): this.type = { for (line <- source.getLines()) { val phrase = line.trim; if (phrase.length > 0 && !phrase.startsWith("#")) MutableLexicon.this.+=(phrase) }; source.close(); this }
  /** All a lines from the input String to this lexicon.  String contains multiple newline-separated lexicon entries */
  def ++=(phrases:String): this.type = ++=(Source.fromString(phrases))
  /** All a lines from the input File to this lexicon.  File contains multiple newline-separated lexicon entries */
  def ++=(file:File, enc:String = "UTF-8"): this.type = ++=(Source.fromFile(file, enc))
  /** Add all lines from the InputStream to this lexicon */
  def ++=(is:InputStream): this.type = this.++=(Source.fromInputStream(is)(Codec.UTF8))
}

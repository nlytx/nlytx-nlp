package cc.factorie.nlp.lexicon

import java.io.File

import cc.factorie.nlp.lemma.{Lemmatizer, LowercaseLemmatizer}
import cc.factorie.app.chain.Observation
import cc.factorie.app.strings.StringSegmenter

import scala.io.Source

/** A lexicon containing single words or multi-word phrases.
  *
  * @author Kate Silverstein
  */
@deprecated("Use TriePhraseLexicon instead", "Before 10/1/15")
class PhraseLexicon(val name: String, val tokenizer: StringSegmenter = cc.factorie.app.strings.nonWhitespaceSegmenter, val lemmatizer: Lemmatizer = LowercaseLemmatizer) extends MutableLexicon {
  def this(file: File) = { this(file.toString, cc.factorie.app.strings.nonWhitespaceSegmenter, LowercaseLemmatizer); this.++=(Source.fromFile(file)(scala.io.Codec.UTF8))}
  val wordTree = new SuffixTree(false)
  def +=(phrase:String): Unit = {
    val words: Seq[String] = tokenizer(phrase).toSeq
    wordTree.add(words.map(lemmatizer.lemmatize(_)))
  }
  /** Checks whether the lexicon contains this already-lemmatized/tokenized single word */
  def containsLemmatizedWord(word: String): Boolean = {
    containsLemmatizedWords(List(word).toSeq)
  }
  /** Checks whether the lexicon contains this already-lemmatized/tokenized phrase, where 'words' can either be
    * single word or a multi-word expression. */
  def containsLemmatizedWords(words: Seq[String]): Boolean = {
    wordTree.contains(words)
  }
  /** Tokenizes and lemmatizes the string of each entry in 'query', then checks if the sequence is in the lexicon*/
  def contains[T<:Observation[T]](query: Seq[T]): Boolean = {
    val strings = query.map(_.string)
    val tokenized = strings.flatMap(tokenizer(_))
    val lemmatized = tokenized.map(lemmatizer.lemmatize(_)).toSeq
    containsLemmatizedWords(lemmatized)
  }
  /** Tokenizes and lemmatizes query.string, then checks if the sequence is in the lexicon */
  def contains[T<:Observation[T]](query: T): Boolean = {
    val tokenized = tokenizer(query.string).toSeq
    val lemmatized = tokenized.map(lemmatizer.lemmatize(_))
    containsLemmatizedWords(lemmatized)
  }
  override def toString(): String = { "<PhraseLexicon with "+wordTree.size+" words>" }

  /** Return length of match, or -1 if no match. */
  def startsAt[T<:Observation[T]](query:T): Int = {
    if (contains(query)){
      val tokenized = tokenizer(query.string).toSeq
      val lemmatized = tokenized.map(lemmatizer.lemmatize(_))
      return wordTree.getSuffixIndex(lemmatized, true)
    }
    -1
  }
}


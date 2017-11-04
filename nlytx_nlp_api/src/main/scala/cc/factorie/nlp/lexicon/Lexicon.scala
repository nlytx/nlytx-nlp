package cc.factorie.nlp.lexicon

import java.io.File

import cc.factorie.nlp.TokenSpan
import cc.factorie.nlp.lemma.{Lemmatizer, LowercaseLemmatizer}
import cc.factorie.app.chain.Observation
import cc.factorie.app.strings.StringSegmenter

import scala.io.Source


/** The general interface to a lexicon.  Both WordLexicon and PhraseLexicon are subclasses.
  *
  * @author Andrew McCallum */
trait Lexicon {
  /** An identifier for this lexicon, suitable for adding as a category to a FeatureVectorVariable[String]. */
  def name: String
  // For pre-processing of lexicon and query strings
  /** The string segmenter that breaks a lexicon entries and queries into (potentially) multi-word phrases. */
  def tokenizer:StringSegmenter
  /** The string lemmatizer that simplifies lexicon entries and queries before searching for a match.
      For example, a common lemmatizer is one that lowercases all strings. */
  def lemmatizer:Lemmatizer
  /** Is this single word in the lexicon?  The input String will not be processed by tokenizer, but will be processed by the lemmatizer. */
  def containsLemmatizedWord(word:String): Boolean
  // For querying the lexicon
  /** Is this single word in the lexicon?  The input String will not be processed by tokenizer, but will be processed by the lemmatizer. */
  def containsWord(word:String): Boolean = containsLemmatizedWord(lemmatizer.lemmatize(word))
  /** Is the pre-tokenized sequence of words in the lexicon?  The input words are expected to already be processed by the lemmatizer. */
  def containsLemmatizedWords(words: Seq[String]): Boolean
  /** Is the pre-tokenized sequence of words in the lexicon?  Each of the input words will be processed by the lemmatizer. */
  def containsWords(words: Seq[String]): Boolean = containsLemmatizedWords(words.map(lemmatizer.lemmatize(_)))
  /** Is this Token (or more generally Observation) a member of a phrase in the lexicon (including single-word phrases)?
      The query.string will be processed by the lemmatizer.
      For example if query.string is "New" and query.next.string is "York" and the two-word phrase "New York" is in the lexicon,
      then this method will return true.  But if query.next.string is "shoes" (and "New shoes" is not in the lexicon) this method will return false. */
  def contains[T<:Observation[T]](query:T): Boolean
  def contains[T<:Observation[T]](query:Seq[T]): Boolean
  def contains(span:TokenSpan): Boolean = contains(span.value)
  /** Is the input String in the lexicon.  The input is tokenized and lemmatized;
      if the tokenizer indicates that it is a multi-word phrase, it will be processed by containsWords, otherwise containsWord. */
  def contains(untokenizedString:String): Boolean = { val words = tokenizer(untokenizedString).map(lemmatizer.lemmatize(_)).toSeq; if (words.length == 1) containsWord(words.head) else containsWords(words) }
}


/** Support for constructing Lexicons
    @author Andrew McCallum */
object Lexicon {
  def fromSource(name:String, source:Source, tokenizer:StringSegmenter = cc.factorie.app.strings.nonWhitespaceSegmenter, lemmatizer:Lemmatizer = LowercaseLemmatizer): Lexicon = {
    var result: MutableLexicon = new PhraseLexicon(name, tokenizer, lemmatizer)
    result ++= source
    source.close()
    result
  }
  def fromFilename(filename:String, tokenizer:StringSegmenter = cc.factorie.app.strings.nonWhitespaceSegmenter, lemmatizer:Lemmatizer = LowercaseLemmatizer): Lexicon =
    fromSource(filename, Source.fromFile(new File(filename))(scala.io.Codec.UTF8))
  def fromResource(resourceFilename:String, tokenizer:StringSegmenter = cc.factorie.app.strings.nonWhitespaceSegmenter, lemmatizer:Lemmatizer = LowercaseLemmatizer): Lexicon =
    fromSource(resourceFilename, Source.fromInputStream(getClass.getResourceAsStream(resourceFilename)))
}

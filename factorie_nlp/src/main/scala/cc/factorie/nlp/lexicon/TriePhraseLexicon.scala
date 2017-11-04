package cc.factorie.nlp.lexicon

import cc.factorie.nlp.Token
import cc.factorie.nlp.lemma.{Lemmatizer, LowercaseLemmatizer}
import cc.factorie.app.chain.Observation
import cc.factorie.app.strings.StringSegmenter
import cc.factorie.variable.CategoricalVectorVar

import scala.io.Source


/**
  * A phrase lexicon based on Aho-Corasick Trie lookups.
  * Use the tag text methods in preference to the other methods, which are preserved for compatibility.
  * The other methods have the same semantics as the PhraseLexicon, which return true iff the whole string is in the lexicon.
  */
class TriePhraseLexicon(val name: String, val tokenizer: StringSegmenter = cc.factorie.app.strings.nonWhitespaceSegmenter, val lemmatizer: Lemmatizer = LowercaseLemmatizer, val sep: String = " ") extends MutableLexicon {
  val trie = new AhoCorasick(sep)

  def +=(phrase:String): Unit = synchronized {
    val words: Seq[String] = tokenizer(phrase).toSeq
    trie += words.map(lemmatizer.lemmatize)
  }

  /** All a lines from the input Source to this lexicon.  Source is assumed to contain multiple newline-separated lexicon entries.
    * Overriden to call setTransitions after reading the file.
    */
  override def ++=(source:Source): this.type = synchronized { for (line <- source.getLines()) { val phrase = line.trim; if (phrase.length > 0) TriePhraseLexicon.this.+=(phrase) }; trie.setTransitions(); source.close(); this }

  def setTransitions() : Unit = synchronized { trie.setTransitions() }

  /** Checks whether the lexicon contains this already-lemmatized/tokenized single word */
  def containsLemmatizedWord(word: String): Boolean = { containsLemmatizedWords(List(word).toSeq) }

  /** Checks whether the lexicon contains this already-lemmatized/tokenized phrase, where 'words' can either be
    * single word or a multi-word expression. */
  def containsLemmatizedWords(words: Seq[String]): Boolean = {
    trie.findExactMention(words)
  }

  /** Tokenizes and lemmatizes the string of each entry in 'query', then checks if the exact sequence is in the lexicon*/
  def contains[T<:Observation[T]](query: Seq[T]): Boolean = {
    val strings = query.map(_.string)
    val tokenized = strings.flatMap(tokenizer(_))
    val lemmatized = tokenized.map(lemmatizer.lemmatize(_)).toSeq
    containsLemmatizedWords(lemmatized)
  }

  /** Tokenizes and lemmatizes query.string, then checks if the exact sequence is in the lexicon */
  def contains[T<:Observation[T]](query: T): Boolean = {
    val tokenized = tokenizer(query.string).toSeq
    val lemmatized = tokenized.map(lemmatizer.lemmatize(_))
    containsLemmatizedWords(lemmatized)
  }

  override def toString(): String = { "<PhraseLexicon with "+trie.size+" words>" }

  /** Tags each token with the specified tag, if it is present in the lexicon */
  def tagLemmatizedText(tokens : Seq[Token], featureFunc : (Token => CategoricalVectorVar[String]), tag : String) : Unit = {
    trie.tagMentions(tokens,featureFunc,tag)
  }

  /** Tags each token with the specified tag, if the lemmatized form is present in the lexicon */
  def tagText(tokens : Seq[Token], featureFunc : (Token => CategoricalVectorVar[String]), tag : String) : Unit = {
    trie.lemmatizeAndTagMentions(tokens,featureFunc,tag,lemmatizer)
  }

  /** Tags each token with the specified tag, if the lemmatized form is present in the lexicon */
  def tagText(tokens : Seq[Token], featureFunc : (Token => CategoricalVectorVar[String]), tag : String, lemmaFunc : (Token => String)) : Unit = {
    trie.tagMentions(tokens,featureFunc,tag,lemmaFunc)
  }
}


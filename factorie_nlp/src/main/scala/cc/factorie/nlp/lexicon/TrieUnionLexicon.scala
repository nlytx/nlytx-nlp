package cc.factorie.nlp.lexicon

import cc.factorie.nlp.Token
import cc.factorie.nlp.lemma.Lemmatizer
import cc.factorie.app.chain.Observation
import cc.factorie.app.strings.StringSegmenter
import cc.factorie.variable.CategoricalVectorVar


/**
  * A union lexicon of multiple TriePhraseLexicons.
  * Has similar semantics to the TriePhraseLexicon.
  */
class TrieUnionLexicon[L <: TriePhraseLexicon](val name: String, val members: L*) extends MutableLexicon {
  def tokenizer: StringSegmenter = members.head.tokenizer
  def lemmatizer: Lemmatizer = members.head.lemmatizer
  def containsLemmatizedWord(word: String): Boolean = members.exists(_.containsLemmatizedWord(word))
  def containsLemmatizedWords(word: Seq[String]): Boolean = members.exists(_.containsLemmatizedWords(word))
  def contains[T<:Observation[T]](query: T): Boolean = members.exists(_.contains(query))
  def contains[T<:Observation[T]](query: Seq[T]): Boolean = members.exists(_.contains(query))
  def +=(s:String): Unit = {throw new Error("TrieUnionLexicon is immutable. Append to the appropriate TriePhraseLexicon.")}
  override def toString(): String = {
    var st = "UNION { "
    members.foreach(st += _.toString()+" , ")
    st += " } "
    st
  }

  def tagLemmatizedText(tokens : Seq[Token], featureFunc : (Token => CategoricalVectorVar[String]), tag : String) : Unit = {
    members.map(_.tagLemmatizedText(tokens,featureFunc,tag))
  }

  def tagText(tokens : Seq[Token], featureFunc : (Token => CategoricalVectorVar[String]), tag : String) : Unit = {
    members.map(_.tagText(tokens,featureFunc,tag))
  }

  /** Tags each token with the specified tag, if the lemmatized form is present in the lexicon */
  def tagText(tokens : Seq[Token], featureFunc : (Token => CategoricalVectorVar[String]), tag : String, lemmaFunc : (Token => String)) : Unit = {
    members.map(_.tagText(tokens,featureFunc,tag,lemmaFunc))
  }
}
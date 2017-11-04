package cc.factorie.nlp.lemma

trait Lemmatizer {
  def lemmatize(word:String): String
}

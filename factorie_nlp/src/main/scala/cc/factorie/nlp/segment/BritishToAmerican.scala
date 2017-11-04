package cc.factorie.nlp.segment

object BritishToAmerican extends scala.collection.mutable.HashMap[String,String] {
  this("colour") = "color"
  // TODO Add more, e.g. see http://oxforddictionaries.com/us/words/british-and-american-spelling
}
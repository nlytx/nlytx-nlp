package cc.factorie.nlp.pos

import cc.factorie.nlp.lexicon.NumberWords

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */
trait ForwardPosDigits {

  val digitsRegex = "\\d+".r
  val containsDigitRegex = ".*\\d.*".r

  def collapseDigits(word:String): String = {
    if (NumberWords.containsWord(word) || containsDigitRegex.findFirstIn(word).nonEmpty) "0" else word
  }
  def replaceDigits(word:String): String = {
    if (NumberWords.containsWord(word)) "<NUM>" else digitsRegex.replaceAllIn(word, "0")
  }
}

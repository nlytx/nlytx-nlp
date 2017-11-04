package cc.factorie.nlp.parse

import cc.factorie.nlp.Sentence

class LightweightParseSentence(s: Sentence){
  val length: Int = s.length + 1
  val _tokens: Array[LightweightParseToken] = new Array[LightweightParseToken](length-1)
  var i = 0; while(i < length-1) { _tokens(i) = new LightweightParseToken(s(i)); i += 1 }
  val parse = s.attr[ParseTree]
  val goldHeads = Seq(-1) ++ parse._targetParents.map(_ + 1)
  val goldLabels = Seq("<ROOT-ROOT>") ++ parse._labels.map(_.target.categoryValue)

  // we are working with the original sentence, with an additional
  // ROOT token that comes at index 0, moving all other indices up by 1:
  // idx < 0 -> NULL_TOKEN
  // idx = 0 -> ROOT_TOKEN
  // 0 < idx < sentence.length+1 -> sentence(idx-1)
  // idx > sentence.length -> NULL_TOKEN
  def apply(idx: Int) = idx match {
    case 0 => RootToken
    case i if (i > 0 && i < length) => _tokens(i-1)
    case _ => NullToken
  }
}

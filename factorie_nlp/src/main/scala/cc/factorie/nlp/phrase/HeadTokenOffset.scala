package cc.factorie.nlp.phrase

import cc.factorie.nlp.lexicon.Preposition
import cc.factorie.nlp.pos.PennPosTag

/** A heuristic for selecting the head of a phrase.
    *If a parse is available, use it to find the head; if a preposition is found, select the word before it; otherwise simply select the last token. */
object HeadTokenOffset {
  def apply(phrase:Phrase): Int = {
    if (phrase.length == 1) return 0
    val span = phrase.value
    val sentence = phrase.sentence
    // If there is a parse, then traverse up the tree until just before we exit the Span
    val parse = sentence.parse
    if (parse ne null) {
      var headSentenceIndex = math.min(span.end, sentence.end)-1 - sentence.start
      var parentSentenceIndex = parse.parentIndex(headSentenceIndex)
      while (span.contains(parentSentenceIndex + sentence.start)) {
        headSentenceIndex = parentSentenceIndex
        parentSentenceIndex = parse.parentIndex(parentSentenceIndex)
      }
      //Sometimes phrases are broken, consisting of more than one subgraph in the parse tree; check if parent of exit is not again part of mention
      if(parentSentenceIndex >= 0) {
        parentSentenceIndex = parse.parentIndex(parentSentenceIndex)
        while (span.contains(parentSentenceIndex + sentence.start)) {
          headSentenceIndex = parentSentenceIndex
          parentSentenceIndex = parse.parentIndex(parentSentenceIndex)
        }
      }
      return headSentenceIndex + sentence.start - span.start
    } else {
      // If there is a preposition, select the word just before the first preposition
      val prepositionIndex = span.indexWhere(Preposition.contains(_))
      if (prepositionIndex >= 1) return prepositionIndex - 1
      // If there is noun, return the last noun
      val lastNounIndex = span.lastIndexWhere(_.attr[PennPosTag].isNoun)
      if (lastNounIndex > 0) return lastNounIndex
      // Otherwise simply select the last word of the span
      else return span.length-1

    }
  }
}

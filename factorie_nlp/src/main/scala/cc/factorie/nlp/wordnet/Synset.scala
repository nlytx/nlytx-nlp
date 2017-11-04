package cc.factorie.nlp.wordnet

import scala.collection.mutable

class Synset(val id: String, val hyps: Set[String], val ants: Set[String], wn: WordNet) {
  def antonyms(): Set[Synset] = this.ants.map(x => wn.allSynsets(x))

  /* get the parent synsets (hypernyms) of this synset */
  def hypernyms(): Set[Synset] = this.hyps.map(x => wn.allSynsets(x))

  /* recursively get all parent synsets (hypernyms) of this synset */
  def allHypernyms(): Set[Synset] = {
    val result = mutable.Set[Synset]()
    def visit(s: Synset) {
      if (!result.contains(s)) {
        result.add(s)
        s.hypernyms().foreach(visit)
      }
    }
    visit(this)
    result.toSet
  }
}

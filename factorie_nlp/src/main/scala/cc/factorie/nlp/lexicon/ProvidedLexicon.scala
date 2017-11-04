package cc.factorie.nlp.lexicon

import cc.factorie.util.ModelProvider

trait ProvidedLexicon[L] {
  this: MutableLexicon =>

  def provider:ModelProvider[L]

  synchronized {
    this.++=(provider.provide)
  }
}
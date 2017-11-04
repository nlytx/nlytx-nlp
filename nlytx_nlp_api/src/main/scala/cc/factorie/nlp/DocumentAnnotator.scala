package cc.factorie.nlp

import cc.factorie.nlp.coref.Mention
import cc.factorie.nlp.phrase.Phrase
import cc.factorie.util.Threading

trait DocumentAnnotator {


  def process(document: Document): Document  // NOTE: this method may mutate and return the same document that was passed in
  def prereqAttrs: Iterable[Class[_]]
  def postAttrs: Iterable[Class[_]]

  def processSequential(documents: Iterable[Document]): Iterable[Document] = documents.map(process)
  def processParallel(documents: Iterable[Document], nThreads: Int = Runtime.getRuntime.availableProcessors()): Iterable[Document] = Threading.parMap(documents, nThreads)(process)


  /** How the annotation of this DocumentAnnotator should be printed in one-word-per-line (OWPL) format.
      If there is no per-token annotation, return null.  Used in Document.owplString. */
  def tokenAnnotationString(token:Token): String

  /** How the annotation of this DocumentAnnotator should be printed as extra information after a one-word-per-line (OWPL) format.
      If there is no document annotation, return the empty string.  Used in Document.owplString. */
  def documentAnnotationString(document:Document): String = ""
  def phraseAnnotationString(phrase:Phrase): String = ""
  def mentionAnnotationString(mention:Mention): String = ""
}

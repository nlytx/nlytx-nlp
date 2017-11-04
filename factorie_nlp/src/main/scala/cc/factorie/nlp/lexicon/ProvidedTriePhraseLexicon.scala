package cc.factorie.nlp.lexicon

import cc.factorie.util.ModelProvider

import scala.reflect.ClassTag

class ProvidedTriePhraseLexicon[L]()(implicit val provider:ModelProvider[L], ct:ClassTag[L]) extends TriePhraseLexicon(ct.runtimeClass.getName) with ProvidedLexicon[L]
package io.nlytx.nlp.annotators

import java.io.Serializable

import cc.factorie.nlp.ner.ConllChainNer


object NerTagger extends ConllChainNer()(ModelLocator.locate[ConllChainNer]("/models/ner/"), EnglishLexiconFeatures) with Serializable
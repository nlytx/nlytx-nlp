package io.nlytx.factorie_nlp.annotators

import java.io.Serializable

import cc.factorie.nlp.ner.ConllChainNer


object NerTagger extends ConllChainNer()(ModelLocator.locate[ConllChainNer]("/models/ner/"), ModelLocator.LexiconFeatures("en")) with Serializable
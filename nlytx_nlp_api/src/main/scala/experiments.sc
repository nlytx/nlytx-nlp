import cc.factorie.nlp.lemma.WordNetLemmatizer
import cc.factorie.nlp.ner.ConllChainNer
import cc.factorie.nlp.pos.OntonotesForwardPosTagger
import cc.factorie.nlp.wordnet.WordNet
import io.nlytx.nlp.annotators.ModelLocator

import scala.io.Source


//val wn = new WordNet(ModelLocator.wordNetStreamFactory)

//val wnl = new WordNetLemmatizer(ModelLocator.wordNetStreamFactory)

//val wn = new WordNet(ModelLocator.wordNetStreamFactory)


val wnl = new WordNetLemmatizer(ModelLocator.wordNetStreamFactory)


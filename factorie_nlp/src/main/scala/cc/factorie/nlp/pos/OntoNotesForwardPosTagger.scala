package cc.factorie.nlp.pos

import java.io.Serializable


/** The default part-of-speech tagger, trained on all Ontonotes training data (including Wall Street Journal), with parameters loaded from resources in the classpath. */
class OntonotesForwardPosTagger(url:java.net.URL) extends ForwardPosTagger(url) with Serializable
object OntonotesForwardPosTagger extends OntonotesForwardPosTagger(cc.factorie.util.ClasspathURL[OntonotesForwardPosTagger](".factorie")) with Serializable

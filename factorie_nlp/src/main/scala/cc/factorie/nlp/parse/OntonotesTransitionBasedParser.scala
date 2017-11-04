package cc.factorie.nlp.parse

class OntonotesTransitionBasedParser(url:java.net.URL) extends TransitionBasedParser(url)
object OntonotesTransitionBasedParser extends OntonotesTransitionBasedParser(cc.factorie.util.ClasspathURL[OntonotesTransitionBasedParser](".factorie"))

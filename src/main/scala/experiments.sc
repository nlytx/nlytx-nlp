import io.nlytx.nlp.lda.{Analyser_LDA, TopicResults}


def lda(indexedText:Map[String,String],numTopics: Int, iterations: Int) = {

  val ldaData = Analyser_LDA.process(indexedText, numTopics, iterations)
  println("Analysed: "+ldaData.numDocuments)
  println("Get topics...")
  val topics = ldaData.getTopics(5)
  println("Get docTopics...")
  val docTopics = ldaData.getDocumentTopics
  println("Build and save topicResults...")
  val topicResults = new TopicResults(topics, docTopics)
  //Check document
  val doc = ldaData.documents.head
  println("file: "+doc.file +"| domain: "+doc.domain.toString+"| theta: "+doc.theta.value.toString()+"| zs: "+doc.zs.toString())

  topicResults

}

val input = Map("1"->"This is document one.",
  "2"->"This is another doc.",
  "3"->"And this one is really different from the others.",
  "4" -> "The sky is blue during the day.",
  "5" -> "Well, I'm not sure if this is working properly. I can't see the topics lining up.")

val result = lda(input,4,10000)

result.topics.foreach { t => println(t)}

result.topicDistribution.foreach {d => println(d)}


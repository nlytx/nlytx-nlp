package io.nlytx.nlp.lda

/**
 * Created by andrew on 28/01/15.
 */
case class TopicResults (
                        //id: String,
                        topics: Map[String,String],
                        topicDistribution:Map[String,Map[String, Double]]
                          ) {


  override def toString: String = {
    //"id: "+id.toString+
      "| topics: "+topics.toString+"| documentTopics: "+topicDistribution.toString
  }
}
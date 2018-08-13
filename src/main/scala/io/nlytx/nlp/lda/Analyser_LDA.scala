package io.nlytx.nlp.lda

import java.util

import cc.factorie.app.strings.alphaSegmenter
import cc.factorie.nlp.lexicon.StopWords
import cc.factorie.directed._
import cc.factorie.variable._
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by andrew on 27/01/15.
 */

//class Analyser_LDA

//@Component
object Analyser_LDA {

  implicit val model = DirectedModel()
  implicit val random = new scala.util.Random(0)

  class Zs(len:Int,numTopics:Int) extends DiscreteSeqVariable(len) {
    object ZDomain extends DiscreteDomain(numTopics)
    object ZSeqDomain extends DiscreteSeqDomain { def elementDomain = ZDomain }
    def domain = ZSeqDomain
  }

  var lda:LdaData = _
  var wordRankMap = new mutable.HashMap[String, Int]
  var topicRankMap = new mutable.HashMap[String,Int]
  var topicMap:Map[Integer,String] = _
  var topicList:java.util.List[java.lang.String] = new util.ArrayList[java.lang.String]

  def logger = LoggerFactory.getLogger(this.getClass)


  def process(records: Map[String,String],numTopics:Int,iterations:Int = 1000) /*:Map[Integer,String] */ = {
    logger.info("numTopics: " + numTopics)
    lda = new LdaData(numTopics)
    //var docCount = 0
    records.foreach { record =>
      val id = record._1
      val text = record._2
      //docCount += 1
      val tokens = alphaSegmenter(text).map(_.toLowerCase).filter(!StopWords.contains(_)).toSeq
      if (tokens.size > 0) {
        val theta = ProportionsVariable.dense(numTopics) ~ Dirichlet(lda.alpha) //Topic distribution for this doc
        val zs = new Zs(tokens.length, numTopics) :~ PlatedDiscrete(theta) //Topics per word for this doc
        var doc = new LdaDocument(text, theta, tokens, lda.WordSeqDomain) ~ PlatedCategoricalMixture(lda.phis, zs)
        doc.id = id
        lda.documents += doc
      }
    }

    logger.info("Read " + lda.numDocuments + " documents with " + lda.numTokens + " tokens")

    val collapse = new ArrayBuffer[Var]
    collapse += lda.phis
    collapse ++= lda.documents.map(_.theta)
    val sampler = new CollapsedGibbsSampler(collapse, model)
    for (i <- 1 to iterations) {
      logger.debug("Iteration "+i)
      for (doc <- lda.documents) {
        sampler.process(doc.zs)
      }

    }
    //lda.topicMap(10)
    lda //.topicList2
  }

  /*
  def wordRankings(numWords:Int = 10):String = {
    lda.phis.foreach(topic => {
      topic.value.top(numWords).foreach(entry => {
        val topicWord = lda.wordDomain.category(entry.index)
        wordRankMap.+=(kv = (topicWord, wordRankMap.getOrElse(topicWord, 0) + 1))
      })
    })
    logger.info("wordRankMap: "+wordRankMap.toSeq.sortBy(_._2).reverse.toString())
    wordRankMap.toSeq.sortBy(_._2).reverse.toString()+"\n\n"
  }

  def topicRankings(numWords:Int = 10):String = {
    var returnString = ""
    lda.phis.foreach(topic => {
      topic.value.top(numWords).foreach(entry => {
        val topicWord = lda.wordDomain.category(entry.index)
        topicRankMap.+=(kv = (topicWord, wordRankMap.getOrElse(topicWord, 0)))
      })
      logger.info("topicRankMap: " + topicRankMap.toSeq.sortBy(_._2).reverse.toString)
      returnString += topicRankMap.toSeq.sortBy(_._2).reverse.toString
    })
    returnString+"\n\n"
  }

  def documentTopics(numWords:Int = 10):String = {
    var docTopicMap = new mutable.HashMap[Int,String]
    for(doc <- lda.documents) {
      var returnString="--------------------------------------------------------------------------------\n"
      returnString+="| "+doc.id+"\n"
      returnString+="Text: "+doc.file+"\n"
      var zMap = new mutable.HashMap[Int,Int]
      for(z <- doc.zs) zMap.+=(kv = (z.intValue,zMap.getOrElse(z.intValue, 0)+1))
      //returnString+="All topics: "+doc.zs.toSeq.toString()
      val topTopic = zMap.toSeq.sortBy(_._2).last._1
      returnString+="Top topic: "+topTopic+" :: "+lda.phis.apply(topTopic).value.top(numWords).map(dp => lda.wordDomain.category(dp.index)).mkString(",")+"\n"
      logger.info("Top topic: "+topTopic+" > "+ lda.phis.apply(topTopic).value.top(20).map(dp => lda.wordDomain.category(dp.index)).mkString(","))
      returnString+="CategoryValues: "+ doc.categoryValues.distinct+"\n\n"
      logger.info("CategoryValues: "+ doc.categoryValues.distinct)
      docTopicMap.+=(kv =(topTopic,docTopicMap.getOrElse(topTopic,"")+returnString))
    }
    docTopicMap.toString()
  }
  */

}

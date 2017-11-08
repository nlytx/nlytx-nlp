package io.nlytx.commons.ranking

import scala.collection.immutable.{ListMap, TreeMap}
import scala.collection.mutable

/**
  * Created by andrew@andrewresearch.net on 14/11/16.
  */
object TfIdf {

  def rankedTerms(docs:List[String], takeTop: Double = 1.0):List[List[String]] = {
    val tfIdf = calculateWeighted(docs, true, takeTop)
    tfIdf.map { d =>
      d.filterNot(_._2 == 0.0).map(_._1).toList
    }
  }

  def calculateWeighted(docs: List[String], ranked:Boolean = true, select: Double = 1.0):List[Map[String,Double]] = {
    val dtc:List[Map[String,Long]] = docs.map(rawTermFrequency)
    val wtc:List[Map[String,Double]] = docs.map(weightedTermFrequency)
    val tdc:Map[String,Long] = termDocCounts(dtc)
    val idf:Map[String,Double] = inverseDocFrequency(tdc,docs.length)
    val tfidf = wtc.map { d =>
      if(ranked) takeTop(select,sortMapByValue(tfIdf(d,idf)))
      else tfIdf(d,idf)
    }
    //System.out.println("ranking: "+ranking)
    tfidf
  }

  def calculateNonWeighted(docs: List[String], ranked:Boolean = true, select: Double = 1.0):List[Map[String,Double]] = {
    val dtc:List[Map[String,Long]] = docs.map(rawTermFrequency)
    val tdc:Map[String,Long] = termDocCounts(dtc)
    val idf:Map[String,Double] = inverseDocFrequency(tdc,docs.length)
    val tfidf = dtc.map { d =>
      val dd = d.map(t => t._1 -> t._2.toDouble)
      if(ranked) takeTop(select,sortMapByValue(tfIdf(dd,idf)))
      else tfIdf(dd,idf)
    }
    //System.out.println("ranking: "+ranking)
    tfidf
  }


  def docWords(docs:List[String]):List[List[String]] = {
    docs.map(splitWords(_))
  }

  def rawTermFrequency(doc:String):Map[String,Long] = {
    splitWords(doc).foldLeft(Map.empty[String, Long]) {
      (count, word) => count + (word -> (count.getOrElse(word, 0L) + 1L))
    }
  }

  def weightedTermFrequency(doc:String):Map[String,Double] = {
    val rtf:Map[String,Long] = rawTermFrequency(doc)
    val max = maxTermFrequency(rtf)
    rtf.map(f => weightedTerm(f._1,f._2,max))
  }

  private def splitWords(text:String):List[String] = text.trim.split("\\W+").toList.map(_.toLowerCase)
  private def maxTermFrequency(termCounts:Map[String,Long]):Long = termCounts.maxBy(_._2)._2
  private def weightedTerm(term:String,count:Long,max:Long):(String,Double) = term -> weightedCount(count,max)
  private def weightedCount(count:Long,max:Long):Double = (0.5 + (0.5 * (count.toDouble / max)))


  def termDocCounts(docTermCounts:List[Map[String,Long]]):Map[String,Long] = {
    docTermCounts.flatten.groupBy(_._1).map(kv => kv._1 -> kv._2.map(_._2).sum)
  }

  def inverseDocFrequency(docCountsPerTerm:Map[String,Long],docsInCollection:Long):Map[String,Double] = {
    docCountsPerTerm.map( dc => dc._1 -> math.log1p(docsInCollection / dc._2))
  }

  def tfIdf(tf:Map[String,Double],idf:Map[String,Double]):Map[String,Double] =  tf.map( t => (t._1, (idf.getOrElse(t._1,0.0) * t._2.toDouble)))

  def sortMapByValue(unsortedMap:Map[String,Double],desc:Boolean = true):Map[String,Double] = {
    val sorted = unsortedMap.toSeq.sortBy(_._2)
    new ListMap[String,Double] ++ (if (desc) sorted.reverse else sorted)
  }

  def takeTop(percent:Double,all:Map[String,Double]):Map[String,Double] = {
    val takeNum = (percent * all.size).toInt
   if (takeNum < 1) all.take(1) else all.take(takeNum)
  }


}

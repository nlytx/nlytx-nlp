/*
 * Copyright (c) 2014.
 *
 * Reflection Analytics Pty Ltd ("COMPANY") CONFIDENTIAL
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains the property of COMPANY.
 * The intellectual and technical concepts contained herein are proprietary to COMPANY and may be covered by
 * Australian and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from COMPANY.  Access to the source code contained herein is hereby forbidden
 * to anyone except current COMPANY employees, managers or contractors who have executed Confidentiality and
 * Non-disclosure agreements explicitly covering such access.
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this
 * source code, which includes information that is confidential and/or proprietary, and is a trade secret, of
 * COMPANY.
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE  OF
 * THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF COMPANY IS STRICTLY PROHIBITED, AND IN VIOLATION
 * OF APPLICABLE LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED
 * INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

package io.nlytx.nlp.lda

import cc.factorie.directed._
import cc.factorie.variable._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * Store all created LDA data
 * Created by andrew on 15/07/2014.
 */
class LdaData(numTopics:Int)(implicit model:MutableDirectedModel, random:Random) {

  var documents  = new ArrayBuffer[LdaDocument]
  val alpha = MassesVariable.dense(numTopics, 0.1) //Parameter for Dirichlet prior on per-doc distribution
  object WordSeqDomain extends CategoricalSeqDomain[String]
  val wordDomain = WordSeqDomain.elementDomain
  val beta = MassesVariable.growableUniform(wordDomain, 0.1) //Parameter for Dirichlet prior on per-topic distribution
  var phis = Mixture(numTopics)(ProportionsVariable.growableDense(wordDomain) ~ Dirichlet(beta)) //Word distribution per topic

  def domain = WordSeqDomain
  def numDocuments = documents.size
  def numTokens = documents.foldLeft(0)(_ + _.size)
  def topicNumber(topic:ProportionsVariable) = phis.indexOf(topic)+1
  def topicWords(topic:ProportionsVariable,numWords:Int) = topic.value.top(numWords).map(dp => wordDomain.category(dp.index)).mkString(", ")



  def getTopics(numWords:Int = 10):Map[String,String] = {
    var topicMap:Map[String,String] = Map()
    phis.foreach(topic => topicMap += (topicNumber(topic).toString -> topicWords(topic, numWords)))
    topicMap.toMap
  }

  def getDocumentTopics:Map[String,Map[String, Double]] = {
    var docTopics:Map[String,Map[String,Double]] = Map()
    documents.foreach{ doc: LdaDocument =>
      val topicDist:Map[String, Double] = ((1 to 20).map(_.toString) zip doc.theta.value.toSeq).toMap[String,Double] //.foreach(t => topicDist += (t.intValue -> (zfs.getOrElse(z.intValue,0) + 1)))
      docTopics += (doc.id -> topicDist)
    }
    docTopics
  }

  def getMixture = {

  }


}

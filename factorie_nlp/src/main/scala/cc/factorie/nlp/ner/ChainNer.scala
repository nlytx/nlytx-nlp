/* Copyright (C) 2008-2016 University of Massachusetts Amherst.
   This file is part of "FACTORIE" (Factor graphs, Imperative, Extensible)
   http://factorie.cs.umass.edu, http://github.com/factorie
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package cc.factorie.nlp.ner

import java.io._

import cc.factorie._
import cc.factorie.nlp.{Document, DocumentAnnotator, Sentence, Token}
import cc.factorie.app.chain.{ChainModel, SegmentEvaluation}
import cc.factorie.optimize.{AdaGrad, ParameterAveraging, Trainer}
import cc.factorie.util.{BinarySerializer, CircularBuffer, JavaHashMap}
import cc.factorie.variable._

import scala.reflect.ClassTag




/**
 * A base class for finite-state named entity recognizers
 */
abstract class ChainNer[L<:NerTag](val labelDomain: CategoricalDomain[String] with SpanEncoding,
                                   val newLabel: (Token, String) => L,
                                   labelToToken: L => Token,
                                   modelIs: InputStream=null,
                                   nerLexiconFeatures: NerLexiconFeatures)(implicit m: ClassTag[L]) extends DocumentAnnotator with Serializable {

  val prereqAttrs = Seq(classOf[Sentence])
  val postAttrs = Seq(m.runtimeClass)

  val FEATURE_PREFIX_REGEX = "^[^@]*$".r

  def process(document:Document) =
    if(document.tokenCount > 0) {
      if (!document.tokens.head.attr.contains(m.runtimeClass))
        document.tokens.map(token => token.attr += newLabel(token, "O"))
      if (!document.tokens.head.attr.contains(classOf[ChainNERFeatures])) {
        document.tokens.map(token => {token.attr += new ChainNERFeatures(token)})
        addFeatures(document, (t:Token)=>t.attr[ChainNERFeatures])
      }
      document.sentences.collect {
        case sentence if sentence.nonEmpty =>
          val vars = sentence.tokens.map(_.attr[L]).toSeq
          model.maximize(vars)(null)
      }
      document
    } else {
      document
    }

  def tokenAnnotationString(token: Token) = token.attr[L].categoryValue

  object ChainNERFeaturesDomain extends CategoricalVectorDomain[String]
  class ChainNERFeatures(val token: Token) extends BinaryFeatureVectorVariable[String] {
    def domain = ChainNERFeaturesDomain
    override def skipNonCategories = true
  }
  class ChainNERModel[Features <: CategoricalVectorVar[String]:ClassTag](featuresDomain: CategoricalVectorDomain[String],
                                                                labelToFeatures: L => Features,
                                                                labelToToken: L => Token,
                                                                tokenToLabel: Token => L)
    extends ChainModel[L, Features, Token](labelDomain, featuresDomain, labelToFeatures, labelToToken, tokenToLabel)

  val model = new ChainNERModel[ChainNERFeatures](ChainNERFeaturesDomain, l => labelToToken(l).attr[ChainNERFeatures], labelToToken, t => t.attr[L])
  val objective = cc.factorie.variable.HammingObjective

  if (modelIs != null) {
    deserialize(modelIs)
    ChainNERFeaturesDomain.freeze()
    println("found model")
  }

  def serialize(stream: java.io.OutputStream): Unit = {
    import cc.factorie.util.CubbieConversions._
    val is = new DataOutputStream(new BufferedOutputStream(stream))
    BinarySerializer.serialize(ChainNERFeaturesDomain.dimensionDomain, is)
    BinarySerializer.serialize(model, is)
    is.close()
  }

  def deserialize(stream: java.io.InputStream): Unit = {
    import cc.factorie.util.CubbieConversions._
    val is = new DataInputStream(new BufferedInputStream(stream))
    BinarySerializer.deserialize(ChainNERFeaturesDomain.dimensionDomain, is)
    BinarySerializer.deserialize(model, is)
    is.close()
  }

  def prefix( prefixSize : Int, cluster : String ) : String = if(cluster.length > prefixSize) cluster.substring(0, prefixSize) else cluster
  val clusters = JavaHashMap[String, String]()


  def addFeatures(document: Document, vf: Token => CategoricalVectorVar[String]): Unit = {
    document.annotators(classOf[ChainNERFeatures]) = ChainNer.this.getClass
    import cc.factorie.app.strings.simplifyDigits
    val tokenSequence = document.tokens.toIndexedSeq

    nerLexiconFeatures.addLexiconFeatures(tokenSequence, vf)
    
    for (token <- document.tokens) {
      val features = vf(token)
      val rawWord = token.string
      val word = simplifyDigits(rawWord).toLowerCase
      features += s"W=$word"
      features += s"SHAPE=${cc.factorie.app.strings.stringShape(rawWord, 2)}"
      if (token.isPunctuation) features += "PUNCTUATION"
      if (clusters.nonEmpty && clusters.contains(rawWord)) {
        features += "CLUS="+prefix(4,clusters(rawWord))
        features += "CLUS="+prefix(6,clusters(rawWord))
        features += "CLUS="+prefix(10,clusters(rawWord))
        features += "CLUS="+prefix(20,clusters(rawWord))
      }
    }

    for (sentence <- document.sentences) {
      cc.factorie.app.chain.Observations.addNeighboringFeatures(sentence.tokens,vf,FEATURE_PREFIX_REGEX,-2,2)
    }

    val tokenBuffer = new CircularBuffer[CategoricalVectorVar[String]](4)
    val stringBuffer = new CircularBuffer[String](4)
    // This is a separate iteration as combining them would be semantically different due to addNeighbouringFeatures().
    for (token <- document.tokens) {
      val tokenStr = token.string
      val tokenFeatures = vf(token)
      val simpleLowerStr = simplifyDigits(tokenStr).toLowerCase()
      if (simpleLowerStr.length < 5){
        tokenFeatures += "P="+cc.factorie.app.strings.prefix(simpleLowerStr, 4)
        tokenFeatures += "S="+cc.factorie.app.strings.suffix(simpleLowerStr, 4)
      }

      val nextStr = "NEXTWINDOW="+simpleLowerStr

      // Add features from window of 4 words before and after
      var i = 0
      while (i < 4) {
        val curTok = tokenBuffer(i)
        if (curTok != null) {
          curTok += nextStr // add next window feature to the token history
        }
        val prevStr = stringBuffer(i)
        if (prevStr != null) {
          tokenFeatures += prevStr // add previous window feature to the current token
        }
        i += 1
      }
      tokenBuffer += vf(token)
      stringBuffer += "PREVWINDOW="+simpleLowerStr
    }

    val tokenMap = JavaHashMap[String,Seq[String]]()
    for (token <- document.tokens) {
      val tokenStr = token.string
      if (token.isCapitalized && token.string.length > 1) {
        if (!tokenMap.contains(tokenStr)) {
          //First mention of this token
          tokenMap += (tokenStr -> vf(token).activeCategories.map(f => "FIRSTMENTION=" + f))
        } else {
          //Add first mention features
          vf(token) ++= tokenMap(tokenStr)
        }
      }
    }

    document.tokens.foreach(t => if (t.string.matches("[A-Za-z]+")) vf(t) ++= t.charNGrams(2,5).map(n => "NGRAM="+n))
  }

  def sampleOutputString(tokens: Iterable[Token]): String = {
    val sb = new StringBuffer
    for (token <- tokens)
      sb.append(
        "%s %20s %10s %10s\n".format(
          if (token.attr[L with LabeledMutableCategoricalVar[String]].valueIsTarget) " " else "*",
          token.string, token.attr[L with LabeledMutableCategoricalVar[String]].target.categoryValue,
          token.attr[L].categoryValue))
    sb.toString
  }

  def train(trainDocs: Seq[Document], testDocs: Seq[Document], rate: Double=0.18, delta: Double=0.066)(implicit random: scala.util.Random): Double = {

    def labels(docs: Iterable[Document]): Iterable[L with LabeledMutableDiscreteVar] = {
      docs.flatMap(doc => doc.tokens.map(_.attr[L with LabeledMutableDiscreteVar]))
    }

    println("initializing training features...")
    (trainDocs ++ testDocs).foreach(_.tokens.map(token => token.attr += new ChainNERFeatures(token)))
    trainDocs.foreach(addFeatures(_, (t:Token)=>t.attr[ChainNERFeatures]))
    ChainNERFeaturesDomain.freeze()
    println("initializing testing features...")
    testDocs.foreach(addFeatures(_, (t:Token)=>t.attr[ChainNERFeatures]))
    println(sampleOutputString(trainDocs.take(20).last.tokens.take(100)))

    val trainLabels = labels(trainDocs).toIndexedSeq
    val testLabels = labels(testDocs).toIndexedSeq
    val labelDomain: CategoricalDomain[String] = trainLabels.head.domain.asInstanceOf[CategoricalDomain[String]]
    (trainLabels ++ testLabels).foreach(_.setRandomly)

    val examples = trainDocs.flatMap(_.sentences.filter(_.length > 1).map(sentence => new model.ChainLikelihoodExample(sentence.tokens.map(_.attr[L with LabeledMutableDiscreteVar]))))
    val optimizer = new AdaGrad(rate=rate, delta=delta) with ParameterAveraging

    def evaluate(){
      val segmentEvaluation = new SegmentEvaluation[L with CategoricalLabeling[String]](
        labelDomain.categories.filter(_.length > 2).map(_.substring(2)),
        "(B|U)-", "(I|L)-"
      )
      trainDocs.foreach(doc => {
        process(doc)
        for (sentence <- doc.sentences) segmentEvaluation += sentence.tokens.map(_.attr[L with CategoricalLabeling[String]])
      })
      println(s"Train accuracy ${objective.accuracy(trainLabels)}")
      println(segmentEvaluation)
      if (testDocs.nonEmpty) {
        val testSegmentEvaluation = new SegmentEvaluation[L with LabeledMutableCategoricalVar[String]](
          labelDomain.categories.filter(_.length > 2).map(_.substring(2)),
          "(B|U)-", "(I|L)-"
        )
        testDocs.foreach(doc => {
          process(doc)
          for (sentence <- doc.sentences) testSegmentEvaluation += sentence.tokens.map(_.attr[L with CategoricalLabeling[String]])
        })
        println(s"Test accuracy ${objective.accuracy(testLabels)}")
        println(testSegmentEvaluation)
      }
      println(model.parameters.tensors.sumInts(t => t.toSeq.count(x => x == 0)).toFloat/model.parameters.tensors.sumInts(_.length)+" sparsity")
    }

    println(s"training with ${examples.length} examples")
    Trainer.onlineTrain(model.parameters, examples, optimizer=optimizer, evaluate=evaluate, maxIterations = 5)

    val finalEval = new SegmentEvaluation[L with LabeledMutableCategoricalVar[String]](labelDomain.categories.filter(_.length > 2).map(_.substring(2)), "(B|U)-", "(I|L)-")
    val buf = new StringBuffer
    buf.append(new LabeledDiscreteEvaluation(testDocs.flatMap(_.tokens.map(_.attr[L with LabeledMutableDiscreteVar]))))
    for (doc <- testDocs; sentence <- doc.sentences) finalEval += sentence.tokens.map(_.attr[L with LabeledMutableCategoricalVar[String]])
    println("final results:")
    println(finalEval)
    finalEval.f1
  }

  def printEvaluation(trainDocs: Iterable[Document], testDocs: Iterable[Document], iteration: String): Double = {
    println(s"TRAIN ${evaluationString(trainDocs)}")
    val result = evaluationString(testDocs)
    println(s"TEST $result")
    result
  }

  def evaluationString(documents: Iterable[Document]): Double = {
    val buf = new StringBuffer
    buf.append(new LabeledDiscreteEvaluation(documents.flatMap(_.tokens.map(_.attr[L with LabeledMutableDiscreteVar]))))
    val segmentEvaluation = new cc.factorie.app.chain.SegmentEvaluation[L with LabeledMutableCategoricalVar[String]](labelDomain.categories.filter(_.length > 2).map(_.substring(2)), "(B|U)-", "(I|L)-")
    for (doc <- documents; sentence <- doc.sentences)
      segmentEvaluation += sentence.tokens.map(_.attr[L with LabeledMutableCategoricalVar[String]])
    println(s"Segment evaluation $segmentEvaluation")
    segmentEvaluation.f1
  }
}
/*
class ChainNerOpts extends cc.factorie.util.CmdOptions with SharedNLPCmdOptions with ModelProviderCmdOptions with DefaultCmdOptions {
  val saveModel = new CmdOption("save-model", "CoNLLChainNer.factorie", "FILE", "Filename for the model (saving a trained model or reading a running model.")
  val serialize = new CmdOption("serialize", true, "BOOLEAN", "Whether to serialize at all")
  val train = new CmdOption("train", List.empty[File], "List[File]", "Filename(s) from which to read training data in CoNLL 2003 one-word-per-lineformat.")
  val test = new CmdOption("test", List.empty[File], "List[File]", "Filename(s) from which to read test data in CoNLL 2003 one-word-per-lineformat.")
  val brownClusFile = new CmdOption("brown", "brownBllipClusters", "FILE", "File containing brown clusters.")
  val trainDir = new CmdOption("train-dir", new File(""), "Dir", "Path to directory of training data.")
  val testDir = new CmdOption("test-dir", new File(""), "Dir", "Path to directory of test data.")
  val rate = new CmdOption("rate", 0.18, "DOUBLE", "learning rate")
  val delta = new CmdOption("delta", 0.066, "DOUBLE", "learning delta")
  val modelFile = new CmdOption("model-file", "", "STRING", "Filename of the serialized model that you want to load.")
  val useTagger = new CmdOption("use-tagger", "", "STRING", "Which tagger? (remove me later)")
  val lexicon = new LexiconsProviderCmdOption("lexicon")
  val lang =      new CmdOption("language", "en", "STRING", "Lexicons language.")
}
*/
/*
object ConllChainNerTrainer extends cc.factorie.util.HyperparameterMain {
  def evaluateParameters(args:Array[String]): Double = {
    val opts = new ChainNerOpts
    implicit val random = new scala.util.Random(0)
    opts.parse(args)
    val ner = new ConllChainNer()(ModelProvider.empty, new StaticLexiconFeatures(new StaticLexicons()(opts.lexicon.value), opts.lang.value))
    if (opts.brownClusFile.wasInvoked) {
      println(s"Reading brown cluster file: ${opts.brownClusFile.value}")
      for (line <- scala.io.Source.fromFile(opts.brownClusFile.value).getLines()) {
        val splitLine = line.split("\t")
        ner.clusters(splitLine(1)) = splitLine(0)
      }
    }
    val trainPortionToTake = if(opts.trainPortion.wasInvoked) opts.trainPortion.value else 1.0
    val testPortionToTake =  if(opts.testPortion.wasInvoked) opts.testPortion.value else 1.0
    val (trainDocsFull, testDocsFull) = if(opts.train.wasInvoked && opts.test.wasInvoked) {
      opts.train.value.flatMap(f => ner.loadDocs(f.getAbsolutePath)).toSeq ->
        opts.test.value.flatMap(f => ner.loadDocs(f.getAbsolutePath)).toSeq
    } else if(opts.trainDir.wasInvoked && opts.testDir.wasInvoked) {
      opts.trainDir.value.listFiles().flatMap(f => ner.loadDocs(f.getAbsolutePath)).toSeq ->
        opts.testDir.value.listFiles().flatMap(f => ner.loadDocs(f.getAbsolutePath)).toSeq
    } else {
      throw new IllegalArgumentException("You must provide values for either --train and --test or --train-dir and --test-dir")
    }
    val trainDocs = trainDocsFull.take((trainDocsFull.length*trainPortionToTake).floor.toInt)
    val testDocs = testDocsFull.take((testDocsFull.length*testPortionToTake).floor.toInt)
    println(s"using training set: ${opts.train.value} ; test set: ${opts.test.value}")
    println(s"$trainPortionToTake of training data; $testPortionToTake of test data:")
    println(s"using ${trainDocs.length} / ${trainDocsFull.length} train docs, ${trainDocs.map(_.tokenCount).sum} tokens")
    println(s"using ${testDocs.length} / ${testDocsFull.length} test docs, ${testDocs.map(_.tokenCount).sum} tokens")

    val ret = ner.train(trainDocs, testDocs, opts.rate.value, opts.delta.value)

    if (opts.serialize.value) {
      println("serializing model to " + opts.saveModel.value)
      ner.serialize(new FileOutputStream(opts.saveModel.value))
    }

    if(opts.targetAccuracy.wasInvoked) cc.factorie.assertMinimalAccuracy(ret,opts.targetAccuracy.value.toDouble)
    ret
  }
} */
/*
object ConllNerOptimizer {
  def main(args: Array[String]) {
    val opts = new ChainNerOpts
    opts.parse(args)
    opts.serialize.setValue(false)
    import cc.factorie.util.LogUniformDoubleSampler
    val rate = HyperParameter(opts.rate, new LogUniformDoubleSampler(1e-3, 1))
    val delta = HyperParameter(opts.delta, new LogUniformDoubleSampler(0.01, 0.1))

    val qs = new cc.factorie.util.QSubExecutor(10, "cc.factorie.app.nlp.ner.ConllChainNerTrainer")
    val optimizer = new cc.factorie.util.HyperParameterSearcher(opts, Seq(rate, delta), qs.execute, 100, 90, 60)
    val result = optimizer.optimize()
    println("Got results: " + result.mkString(" "))
    println("Best rate: " + opts.rate.value + " best delta: " + opts.delta.value)
    println("Running best configuration...")
    opts.serialize.setValue(true)
    import scala.concurrent.Await
    import scala.concurrent.duration._
    Await.result(qs.execute(opts.values.flatMap(_.unParse).toArray), 1.hours)
    println("Done.")
  }
}
*/

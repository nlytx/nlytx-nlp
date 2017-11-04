package cc.factorie.nlp.pos

import java.io._

import cc.factorie.nlp._
import cc.factorie.nlp.segment.PlainNormalizedTokenString
import cc.factorie.app.classify.backend.LinearMulticlassClassifier
import cc.factorie.la.{SparseBinaryTensor1, WeightsMapAccumulator}
import cc.factorie.optimize.Trainer
import cc.factorie.util._
import cc.factorie.variable.{BinaryFeatureVectorVariable, CategoricalVectorDomain}
import cc.factorie.{Tensor1, la, optimize}

/** A part-of-speech tagger that predicts by greedily labeling each word in sequence.
  * Although it does not use Viterbi, it is surprisingly accurate.  It is also fast.
  **
  *For the Viterbi-based part-of-speech tagger, see ChainPosTagger.
  *
  *@author Andrew McCallum, */
class ForwardPosTagger extends DocumentAnnotator with ForwardPosDigits with Serializable {
  private val logger = Logger.getLogger(this.getClass.getName)

  // Different ways to load saved parameters
  def this(stream:InputStream) = { this(); deserialize(stream) }
  def this(file: File) = {
    this(new FileInputStream(file))
    logger.debug("ForwardPosTagger loading from "+file.getAbsolutePath)
  }
  def this(url:java.net.URL) = {
    this()
    val stream = url.openConnection.getInputStream
    if (stream.available <= 0) throw new Error("Could not open "+url)
    logger.debug("ForwardPosTagger loading from "+url)
    deserialize(stream)
  }

  object FeatureDomain extends CategoricalVectorDomain[String]
  class FeatureVariable(t:Tensor1) extends BinaryFeatureVectorVariable[String] { def domain = FeatureDomain; set(t)(null) } // Only used for printing diagnostics
  lazy val model = new LinearMulticlassClassifier(PennPosDomain.size, FeatureDomain.dimensionSize)

  /** Local lemmatizer used for POS features. */
  protected def lemmatize(string:String): String = replaceDigits(string)
  /** A special IndexedSeq[String] that will return "null" for indices out of bounds, rather than throwing an error */
  class Lemmas(tokens:Seq[Token]) extends IndexedSeq[String] {
    val inner: IndexedSeq[String] = tokens.toIndexedSeq.map((t:Token) => replaceDigits(t.string))
    val innerlc = inner.map(_.toLowerCase)
    val length: Int = inner.length
    def apply(i:Int): String = if (i < 0 || i > length-1) null else inner(i)
    def lc(i:Int): String = if (i < 0 || i > length-1) null else innerlc(i)
    def docFreq(i:Int): String = if ((i < 0 || i > length-1) || !WordData.docWordCounts.contains(innerlc(i))) null else inner(i)
    def docFreqLc(i:Int): String = if (i < 0 || i > length-1 || !WordData.docWordCounts.contains(innerlc(i))) null else innerlc(i)
  }
  protected def lemmas(tokens:Seq[Token]) = new Lemmas(tokens)

  // This should not be a singleton object, global mutable state is bad -luke
  /** Infrastructure for building and remembering a list of training data words that nearly always have the same POS tag.
      Used as cheap "stacked learning" features when looking-ahead to words not yet predicted by this POS tagger.
      The key into the ambiguityClasses is app.strings.replaceDigits().toLowerCase */
  object WordData {
    val ambiguityClasses = JavaHashMap[String,String]()
    val sureTokens = JavaHashMap[String,Int]()
    var docWordCounts = JavaHashMap[String,Int]()
    val ambiguityClassThreshold = 0.4
    val wordInclusionThreshold = 1
    val sureTokenThreshold = -1		// -1 means don't consider any tokens "sure"

    def computeWordFormsByDocumentFrequency(tokens: Iterable[Token], cutoff: Integer, numToksPerDoc: Int) = {
      var begin = 0
      for(i <- numToksPerDoc to tokens.size by numToksPerDoc){
        val docTokens = tokens.slice(begin,i)
        val docUniqueLemmas = docTokens.map(x => lemmatize(x.string).toLowerCase).toSet
        for(lemma <- docUniqueLemmas){
          if (!docWordCounts.contains(lemma)) {
            docWordCounts(lemma) = 0
          }
          docWordCounts(lemma) += 1
        }
        begin = i
      }

      // deal with last chunk of sentences
      if(begin < tokens.size){
        val docTokens = tokens.slice(begin,tokens.size)
        val docUniqueLemmas = docTokens.map(x => lemmatize(x.string).toLowerCase).toSet
        for(lemma <- docUniqueLemmas){
          if (!docWordCounts.contains(lemma)) {
            docWordCounts(lemma) = 0
          }
          docWordCounts(lemma) += 1
        }
      }
      docWordCounts = docWordCounts.filter(_._2 > cutoff)
    }

    def computeAmbiguityClasses(tokens: Iterable[Token]) = {
      val posCounts = collection.mutable.HashMap[String,Array[Int]]()
      val wordCounts = collection.mutable.HashMap[String,Double]()
      var tokenCount = 0
      val lemmas = docWordCounts.keySet
      tokens.foreach(t => {
        tokenCount += 1
        if (t.attr[PennPosTag] eq null) {
          println("POS1.WordData.preProcess tokenCount "+tokenCount)
          println("POS1.WordData.preProcess token "+t.prev.string+" "+t.prev.attr)
          println("POS1.WordData.preProcess token "+t.string+" "+t.attr)
          throw new Error("Found training token with no PennPosTag.")
        }
        val lemma = lemmatize(t.string).toLowerCase
        if (!wordCounts.contains(lemma)) {
          wordCounts(lemma) = 0
          posCounts(lemma) = Array.fill(PennPosDomain.size)(0)
        }
        wordCounts(lemma) += 1
        posCounts(lemma)(t.attr[PennPosTag].intValue) += 1
      })
      lemmas.foreach(w => {
        val posFrequencies = posCounts(w).map(_/wordCounts(w))
        val bestPosTags = posFrequencies.zip(PennPosDomain.categories).filter(_._1 > ambiguityClassThreshold).unzip._2
        val ambiguityString = bestPosTags.mkString("_")
        ambiguityClasses(w) = ambiguityString
        if (wordCounts(w) >= 1000) {
          posFrequencies.zipWithIndex.filter(i => i._1 >= 0.999).foreach(c => sureTokens(w) = c._2)
        }
      })
    }
  }

  def features(token:Token, lemmaIndex:Int, lemmas:Lemmas): SparseBinaryTensor1 = {
    def lemmaStringAtOffset(offset:Int): String = "L@"+offset+"="+lemmas.docFreqLc(lemmaIndex + offset) // this is lowercased
    def wordStringAtOffset(offset:Int): String = "W@"+offset+"="+lemmas.docFreq(lemmaIndex + offset) // this is not lowercased, but still has digits replaced
    def affinityTagAtOffset(offset:Int): String = "A@"+offset+"="+WordData.ambiguityClasses.getOrElse(lemmas.lc(lemmaIndex + offset), null)
    def posTagAtOffset(offset:Int): String = { val t = token.next(offset); "P@"+offset+(if (t ne null) t.attr[PennPosTag].categoryValue else null) }
    def takePrefix(s:String, n:Int): String = {if (n <= s.length) "PREFIX="+s.substring(0,n) else null }
    def takeSuffix(s:String, n:Int): String = { val l = s.length; if (n <= l) "SUFFIX="+s.substring(l-n,l) else null }
    val tensor = new SparseBinaryTensor1(FeatureDomain.dimensionSize); tensor.sizeHint(40)
    def addFeature(s:String): Unit = if (s ne null) { val i = FeatureDomain.dimensionDomain.index(s); if (i >= 0) tensor += i }
    // Original word, with digits replaced, no @
    val Wm2 = if (lemmaIndex > 1) lemmas(lemmaIndex-2) else ""
    val Wm1 = if (lemmaIndex > 0) lemmas(lemmaIndex-1) else ""
    val W = lemmas(lemmaIndex)
    val Wp1 = if (lemmaIndex < lemmas.length-1) lemmas(lemmaIndex+1) else ""
    val Wp2 = if (lemmaIndex < lemmas.length-2) lemmas(lemmaIndex+2) else ""
    // Original words at offsets, with digits replaced, marked with @
    val wm3 = wordStringAtOffset(-3)
    val wm2 = wordStringAtOffset(-2)
    val wm1 = wordStringAtOffset(-1)
    val w0 = wordStringAtOffset(0)
    val wp1 = wordStringAtOffset(1)
    val wp2 = wordStringAtOffset(2)
    val wp3 = wordStringAtOffset(3)
    // Lemmas at offsets
    val lm2 = lemmaStringAtOffset(-2)
    val lm1 = lemmaStringAtOffset(-1)
    val l0 = lemmaStringAtOffset(0)
    val lp1 = lemmaStringAtOffset(1)
    val lp2 = lemmaStringAtOffset(2)
    // Affinity classes at next offsets
    val a0 = affinityTagAtOffset(0)
    val ap1 = affinityTagAtOffset(1)
    val ap2 = affinityTagAtOffset(2)
    val ap3 = affinityTagAtOffset(3)
    // POS tags at prev offsets
    val pm1 = posTagAtOffset(-1)
    val pm2 = posTagAtOffset(-2)
    val pm3 = posTagAtOffset(-3)
    addFeature(wm3)
    addFeature(wm2)
    addFeature(wm1)
    addFeature(w0)
    addFeature(wp1)
    addFeature(wp2)
    addFeature(wp3)
    // The paper also includes wp3 and wm3

    // not in ClearNLP
    //    addFeature(lp3)
    //    addFeature(lp2)
    //    addFeature(lp1)
    //    addFeature(l0)
    //    addFeature(lm1)
    //    addFeature(lm2)
    //    addFeature(lm3)

    addFeature(pm3)
    addFeature(pm2)
    addFeature(pm1)
    addFeature(a0)
    addFeature(ap1)
    addFeature(ap2)
    addFeature(ap3)
    addFeature(lm2+lm1)
    addFeature(lm1+l0)
    addFeature(l0+lp1)
    addFeature(lp1+lp2)
    addFeature(lm1+lp1)
    addFeature(pm2+pm1)
    addFeature(ap1+ap2)
    addFeature(pm1+ap1)

    //    addFeature(pm1+a0) // Not in http://www.aclweb.org/anthology-new/P/P12/P12-2071.pdf
    //    addFeature(a0+ap1) // Not in http://www.aclweb.org/anthology-new/P/P12/P12-2071.pdf

    addFeature(lm2+lm1+l0)
    addFeature(lm1+l0+lp1)
    addFeature(l0+lp1+lp2)
    addFeature(lm2+lm1+lp1)
    addFeature(lm1+lp1+lp2)
    addFeature(pm2+pm1+a0)
    addFeature(pm1+a0+ap1)
    addFeature(pm2+pm1+ap1)
    addFeature(pm1+ap1+ap2)

    //    addFeature(a0+ap1+ap2) // Not in http://www.aclweb.org/anthology-new/P/P12/P12-2071.pdf

    addFeature(takePrefix(W, 1))
    addFeature(takePrefix(W, 2))
    addFeature(takePrefix(W, 3))

    // not in ClearNLP
    //    addFeature("PREFIX2@1="+takePrefix(Wp1, 2))
    //    addFeature("PREFIX3@1="+takePrefix(Wp1, 3))
    //    addFeature("PREFIX2@2="+takePrefix(Wp2, 2))
    //    addFeature("PREFIX3@2="+takePrefix(Wp2, 3))

    addFeature(takeSuffix(W, 1))
    addFeature(takeSuffix(W, 2))
    addFeature(takeSuffix(W, 3))
    addFeature(takeSuffix(W, 4))

    // not in ClearNLP
    //    addFeature("SUFFIX1@1="+takeRight(Wp1, 1))
    //    addFeature("SUFFIX2@1="+takeRight(Wp1, 2))
    //    addFeature("SUFFIX3@1="+takeRight(Wp1, 3))
    //    addFeature("SUFFIX4@1="+takeRight(Wp1, 4))
    //    addFeature("SUFFIX2@2="+takeRight(Wp2, 2))
    //    addFeature("SUFFIX3@2="+takeRight(Wp2, 3))
    //    addFeature("SUFFIX4@2="+takeRight(Wp2, 4))
    addFeature("SHAPE@-2="+cc.factorie.app.strings.stringShape(Wm2, 2))
    addFeature("SHAPE@-1="+cc.factorie.app.strings.stringShape(Wm1, 2))
    addFeature("SHAPE@0="+cc.factorie.app.strings.stringShape(W, 2))
    addFeature("SHAPE@1="+cc.factorie.app.strings.stringShape(Wp1, 2))
    addFeature("SHAPE@2="+cc.factorie.app.strings.stringShape(Wp2, 2))
    // TODO(apassos): add the remaining jinho features not contained in shape
    addFeature("HasPeriod="+(w0.indexOf('.') >= 0))
    addFeature("HasHyphen="+(w0.indexOf('-') >= 0))
    addFeature("HasDigit="+(l0.indexOf('0', 4) >= 0)) // The 4 is to skip over "W@0="
    //addFeature("MiddleHalfCap="+token.string.matches(".+1/2[A-Z].*")) // Paper says "contains 1/2+capital(s) not at the beginning".  Strange feature.  Why? -akm
    tensor
  }
  def features(tokens:Seq[Token]): Seq[SparseBinaryTensor1] = {
    val lemmaStrings = lemmas(tokens)
    tokens.zipWithIndex.map({case (t:Token, i:Int) => features(t, i, lemmaStrings)})
  }

  var exampleSetsToPrediction = false
  class SentenceClassifierExample(val tokens:Seq[Token], model:LinearMulticlassClassifier, lossAndGradient: optimize.OptimizableObjectives.Multiclass) extends optimize.Example {
    def accumulateValueAndGradient(value: DoubleAccumulator, gradient: WeightsMapAccumulator) {
      val lemmaStrings = lemmas(tokens)
      for (index <- 0 until tokens.length) {
        val token = tokens(index)
        val posLabel = token.attr[LabeledPennPosTag]
        val featureVector = features(token, index, lemmaStrings)
        new optimize.PredictorExample(model, featureVector, posLabel.target.intValue, lossAndGradient, 1.0).accumulateValueAndGradient(value, gradient)
        if (exampleSetsToPrediction) {
          posLabel.set(model.classification(featureVector).bestLabelIndex)(null)
        }
      }
    }
  }

  def predict(tokens: Seq[Token]): Unit = {
    val lemmaStrings = lemmas(tokens)
    for (index <- 0 until tokens.length) {
      val token = tokens(index)
      if (token.attr[PennPosTag] eq null) token.attr += new PennPosTag(token, "NNP")
      val l = lemmatize(token.string).toLowerCase
      if (WordData.sureTokens.contains(l)) {
        token.attr[PennPosTag].set(WordData.sureTokens(l))(null)
      } else {
        val featureVector = features(token, index, lemmaStrings)
        token.attr[PennPosTag].set(model.classification(featureVector).bestLabelIndex)(null)
      }
    }
  }
  def predict(span: TokenSpan): Unit = predict(span.tokens)
  def predict(document: Document): Unit = {
    for (section <- document.sections)
      if (section.hasSentences) document.sentences.foreach(predict(_))  // we have Sentence boundaries
      else predict(section.tokens) // we don't // TODO But if we have trained with Sentence boundaries, won't this hurt accuracy?
  }

  // Serialization
  def serialize(filename: String): Unit = {
    val file = new File(filename); if (file.getParentFile ne null) file.getParentFile.mkdirs()
    serialize(new java.io.FileOutputStream(file))
  }
  def deserialize(file: File): Unit = {
    require(file.exists(), "Trying to load non-existent file: '" +file)
    deserialize(new java.io.FileInputStream(file))
  }
  def serialize(stream: java.io.OutputStream): Unit = {
    import CubbieConversions._
    val sparseEvidenceWeights = new la.DenseLayeredTensor2(model.weights.value.dim1, model.weights.value.dim2, new la.SparseIndexedTensor1(_))
    model.weights.value.foreachElement((i, v) => if (v != 0.0) sparseEvidenceWeights += (i, v))
    model.weights.set(sparseEvidenceWeights)
    val dstream = new java.io.DataOutputStream(new BufferedOutputStream(stream))
    BinarySerializer.serialize(FeatureDomain.dimensionDomain, dstream)
    BinarySerializer.serialize(model, dstream)
    BinarySerializer.serialize(WordData.ambiguityClasses, dstream)
    BinarySerializer.serialize(WordData.sureTokens, dstream)
    BinarySerializer.serialize(WordData.docWordCounts, dstream)
    dstream.close()  // TODO Are we really supposed to close here, or is that the responsibility of the caller
  }
  def deserialize(stream: java.io.InputStream): Unit = {
    import CubbieConversions._
    val dstream = new java.io.DataInputStream(new BufferedInputStream(stream))
    BinarySerializer.deserialize(FeatureDomain.dimensionDomain, dstream)
    model.weights.set(new la.DenseLayeredTensor2(FeatureDomain.dimensionDomain.size, PennPosDomain.size, new la.SparseIndexedTensor1(_)))
    BinarySerializer.deserialize(model, dstream)
    BinarySerializer.deserialize(WordData.ambiguityClasses, dstream)
    BinarySerializer.deserialize(WordData.sureTokens, dstream)
    BinarySerializer.deserialize(WordData.docWordCounts, dstream)
    dstream.close()  // TODO Are we really supposed to close here, or is that the responsibility of the caller
  }

  def printAccuracy(sentences: Iterable[Sentence], extraText: String) = {
    val (tokAcc, senAcc, speed, _) = accuracy(sentences)
    println(extraText + s"$tokAcc token accuracy, $senAcc sentence accuracy, $speed tokens/sec")
  }

  def accuracy(sentences:Iterable[Sentence]): (Double, Double, Double, Double) = {
    var tokenTotal = 0.0
    var tokenCorrect = 0.0
    var totalTime = 0.0
    var sentenceCorrect = 0.0
    var sentenceTotal = 0.0
    sentences.foreach(s => {
      var thisSentenceCorrect = 1.0
      val t0 = System.currentTimeMillis()
      process(s) //predict(s)
      totalTime += (System.currentTimeMillis()-t0)
      for (token <- s.tokens) {
        tokenTotal += 1
        if (token.attr[LabeledPennPosTag].valueIsTarget) tokenCorrect += 1.0
        else thisSentenceCorrect = 0.0
      }
      sentenceCorrect += thisSentenceCorrect
      sentenceTotal += 1.0
    })
    val tokensPerSecond = (tokenTotal/totalTime)*1000.0
    (tokenCorrect/tokenTotal, sentenceCorrect/sentenceTotal, tokensPerSecond, tokenTotal)
  }

  def test(sentences:Iterable[Sentence]) = {
    println("Testing on " + sentences.size + " sentences...")
    val (tokAccuracy, sentAccuracy, speed, tokens) = accuracy(sentences)
    println("Tested on " + tokens + " tokens at " + speed + " tokens/sec")
    println("Token accuracy: " + tokAccuracy)
    println("Sentence accuracy: " + sentAccuracy)
  }

  def train(trainSentences:Seq[Sentence], testSentences:Seq[Sentence], lrate:Double = 0.1, decay:Double = 0.01, cutoff:Int = 2, doBootstrap:Boolean = true, useHingeLoss:Boolean = false, numIterations: Int = 5, l1Factor:Double = 0.000001, l2Factor:Double = 0.000001)(implicit random: scala.util.Random) {
    // TODO Accomplish this TokenNormalization instead by calling POS3.preProcess
    //for (sentence <- trainSentences ++ testSentences; token <- sentence.tokens) cc.factorie.app.nlp.segment.PlainTokenNormalizer.processToken(token)

    val toksPerDoc = 5000
    WordData.computeWordFormsByDocumentFrequency(trainSentences.flatMap(_.tokens), 1, toksPerDoc)
    WordData.computeAmbiguityClasses(trainSentences.flatMap(_.tokens))

    // Prune features by count
    FeatureDomain.dimensionDomain.gatherCounts = true
    for (sentence <- trainSentences) features(sentence.tokens) // just to create and count all features
    FeatureDomain.dimensionDomain.trimBelowCount(cutoff)
    FeatureDomain.freeze()
    println("After pruning using %d features.".format(FeatureDomain.dimensionDomain.size))

    /* Print out some features (for debugging) */
    //println("ForwardPosTagger.train\n"+trainSentences(3).tokens.map(_.string).zip(features(trainSentences(3).tokens).map(t => new FeatureVariable(t).toString)).mkString("\n"))

    def evaluate() {
      exampleSetsToPrediction = doBootstrap
      printAccuracy(trainSentences, "Training: ")
      printAccuracy(testSentences, "Testing: ")
      println(s"Sparsity: ${model.weights.value.toSeq.count(_ == 0).toFloat/model.weights.value.length}")
    }
    val examples = trainSentences.shuffle.par.map(sentence =>
      new SentenceClassifierExample(sentence.tokens, model, if (useHingeLoss) cc.factorie.optimize.OptimizableObjectives.hingeMulticlass else cc.factorie.optimize.OptimizableObjectives.sparseLogMulticlass)).seq
    //val optimizer = new cc.factorie.optimize.AdaGrad(rate=lrate)
    val optimizer = new cc.factorie.optimize.AdaGradRDA(rate=lrate, l1=l1Factor/examples.length, l2=l2Factor/examples.length)
    Trainer.onlineTrain(model.parameters, examples, maxIterations=numIterations, optimizer=optimizer, evaluate=evaluate, useParallelTrainer = false)
    if (false) {
      // Print test results to file
      val source = new java.io.PrintStream(new File("pos1-test-output.txt"))
      for (s <- testSentences) {
        for (t <- s.tokens) { val p = t.attr[LabeledPennPosTag]; source.println("%s %20s  %6s %6s".format(if (p.valueIsTarget) " " else "*", t.string, p.target.categoryValue, p.categoryValue)) }
        source.println()
      }
      source.close()
    }
  }

  def process(d: Document) = {
    predict(d)
    if (!d.annotators.contains(classOf[PennPosTag])) d.annotators(classOf[PennPosTag]) = this.getClass
    d
  }
  def process(s: Sentence) = { predict(s); s }
  def prereqAttrs: Iterable[Class[_]] = List(classOf[Token], classOf[Sentence], classOf[PlainNormalizedTokenString])
  def postAttrs: Iterable[Class[_]] = List(classOf[PennPosTag])
  override def tokenAnnotationString(token:Token): String = { val label = token.attr[PennPosTag]; if (label ne null) label.categoryValue else "(null)" }
}


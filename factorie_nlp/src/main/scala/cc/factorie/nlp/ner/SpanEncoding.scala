package cc.factorie.nlp.ner

import cc.factorie.variable.CategoricalDomain

import scala.collection.JavaConverters._

/** Base trait for label span encodings like BILOU and BIO
  *
  * @author Kate Silverstein
  */
trait SpanEncoding {
  this: CategoricalDomain[String] =>
  def prefixes: Set[String]
  def encodedTags(baseTags: Seq[String]): Seq[String] = Seq("O") ++ baseTags.filter(_ != "O").flatMap(t => prefixes.map(_ + t))
  def suffixIntVal(i: Int): Int = if (i == 0) 0 else ((i - 1)/prefixes.size)+1
  def isLicitTransition(from:String, to:String):Boolean

  def isLicit(from:this.type#Value, to:this.type#Value):Boolean

  final def permittedMask:Set[(Int, Int)] =
    (for(t1 <- this._indices.values().asScala; // todo there has to be a better way to get this
         t2 <- this._indices.values().asScala
         if isLicit(t1, t2)) yield {
      //println(s"${t1.category} -> ${t2.category}")
      t1.intValue -> t2.intValue }).toSet
}
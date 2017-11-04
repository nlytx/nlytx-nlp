package cc.factorie.nlp.ner

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */
trait BilouNerDomain {

  //This function was in nlp package object
  //Now we can mix it in to BilouConllNerDomain and BilouOntonnotesNerDomain
  def bilouBoundaries(labels:Seq[String]): Seq[(Int,Int,String)] = {
    val result = new scala.collection.mutable.ArrayBuffer[(Int,Int,String)]
    val strings = labels.map(_.split('-'))
    val bilous = strings.map(_.apply(0))
    val types = strings.map(a => if (a.length > 1) a(1) else "")
    var start = -1; var prevType = ""
    for (i <- 0 until labels.length) {
      val atBoundary = types(i) != prevType || bilous(i) == "B" || bilous(i) == "U"
      if (bilous(i) == "U") result.+=((i, 1, types(i)))
      else if (start >= 0 && atBoundary) { result.+=((start, i-start, types(i-1))); start = -1 }
      if (types(i) != "" && atBoundary){
        start = i
        if (i == labels.length-1)
          result.+=((start, 1, types(i)))
      }
      prevType = types(i)
    }
    result
  }
}

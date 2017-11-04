package cc.factorie.nlp.parse

import scala.annotation.tailrec
import scala.collection.mutable.Set


class ParseState(var stack: Int, var input: Int, val reducedIds: Set[Int], val sentence: LightweightParseSentence) {
  val parseSentenceLength = sentence.length

  val headIndices = Array.fill[Int](parseSentenceLength)(-1)
  val arcLabels = Array.fill[String](parseSentenceLength)("")

  val leftmostDeps = Array.fill[Int](parseSentenceLength)(-1)
  val rightmostDeps = Array.fill[Int](parseSentenceLength)(-1)

  def goldHeads = sentence.goldHeads
  def goldLabels = sentence.goldLabels

  def setHead(tokenIndex: Int, headIndex: Int, label: String) {
    // set head
    headIndices(tokenIndex) = headIndex
    arcLabels(tokenIndex) = label

    // update left and rightmost dependents
    if(headIndex != -1){
      if (tokenIndex < headIndex)
        leftmostDeps(headIndex) = tokenIndex
      else
        rightmostDeps(headIndex) = tokenIndex
    }
  }

  @tailrec final def isDescendantOf(firstIndex: Int, secondIndex: Int): Boolean = {
    val firstHeadIndex = headIndices(firstIndex)
    if (firstHeadIndex == -1) false // firstIndex has no head, so it can't be a descendant
    else if (headIndices(firstHeadIndex) == secondIndex) true
    else isDescendantOf(firstHeadIndex, secondIndex)
  }

  def leftmostDependent(tokenIndex: Int): Int = {
    if (tokenIndex == -1) -1
    else leftmostDeps(tokenIndex)
  }

  def rightmostDependent(tokenIndex: Int): Int = {
    if (tokenIndex == -1) -1
    else rightmostDeps(tokenIndex)
  }

  def leftmostDependent2(tokenIndex: Int): Int = {
    if (tokenIndex == -1) -1
    else{
      val i = leftmostDeps(tokenIndex)
      if (i == -1) -1
      else leftmostDeps(i)
    }
  }

  def rightmostDependent2(tokenIndex: Int): Int = {
    if (tokenIndex == -1) -1
    else {
      val i = rightmostDeps(tokenIndex)
      if (i == -1) -1
      else rightmostDeps(i)
    }
  }

  def leftNearestSibling(tokenIndex: Int): Int = {
    val tokenHeadIndex = headIndices(tokenIndex)
    if(tokenHeadIndex != -1){
      var i = tokenIndex - 1
      while(i >= 0){
        if (headIndices(i) != -1 && headIndices(i) == tokenHeadIndex)
          return i
        i -= 1
      }
    }
    -1
  }

  def rightNearestSibling(tokenIndex: Int): Int = {
    val tokenHeadIndex = headIndices(tokenIndex)
    if(tokenHeadIndex != -1){
      var i = tokenIndex + 1
      while(i < parseSentenceLength){
        if(headIndices(i) != -1 && headIndices(i) == tokenHeadIndex)
          return i
        i += 1
      }
    }
    -1
  }

  def inputToken(offset: Int): Int = {
    val i = input + offset
    if (i < 0 || parseSentenceLength - 1 < i) -1
    else i
  }

  def lambdaToken(offset: Int): Int = {
    val i = stack + offset
    if (i < 0 || parseSentenceLength - 1 < i) -1
    else i
  }

  def stackToken(offset: Int): Int = {
    if (offset == 0)
      return stack

    var off = math.abs(offset)
    var dir = if (offset < 0) -1 else 1
    var i = stack + dir
    while (0 < i && i < input) {
      if (!reducedIds.contains(i)) {
        off -= 1
        if (off == 0)
          return i
      }
      i += dir
    }
    -1
  }
}

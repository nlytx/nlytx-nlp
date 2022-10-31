package io.nlytx.expressions.data

case class CodedSentence(
                          index: Int,
                          sentence: String,
                          metacognitionTags:Vector[String],
                          subTags:Vector[String],
                          phraseTags:Vector[String],
                          selfRatio:Double,
                          othersRatio:Double,
                          phrases:Vector[SentencePhrase]
                        )

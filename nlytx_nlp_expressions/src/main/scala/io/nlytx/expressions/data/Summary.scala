package io.nlytx.expressions.data

case class Summary(
                    metaTagSummary:Map[String,Int],
                    phraseTagSummary:Map[String,Int]
                  )

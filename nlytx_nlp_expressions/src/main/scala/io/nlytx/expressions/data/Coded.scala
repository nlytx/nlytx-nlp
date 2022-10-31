package io.nlytx.expressions.data

case class Coded(
                  sentence:String,
                  phrases:Vector[String],
                  subTags:Vector[String],
                  metaTags:Vector[String]
                )

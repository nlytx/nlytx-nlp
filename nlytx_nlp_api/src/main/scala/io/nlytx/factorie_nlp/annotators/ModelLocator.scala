package io.nlytx.factorie_nlp.annotators

import java.io.InputStream
import java.net.URL

import cc.factorie.nlp.lexicon.{LexiconsProvider, StaticLexicons}
import cc.factorie.nlp.ner.StaticLexiconFeatures
import cc.factorie.util.ISAble.buffered
import cc.factorie.util.ModelProvider
import io.nlytx.factorie_nlp.api.AnnotatorPipelines

import scala.reflect.ClassTag

/**
  * Created by andrew@andrewresearch.net on 3/11/17.
  */

object ModelLocator {

  val logger = AnnotatorPipelines.logger

  val resourcePath = getClass.getResource("/")

  def locate[C](prefix:String="/models/",suffix:String=".factorie")(implicit m: ClassTag[C]): java.net.URL = {
    val newResource = prefix + m.runtimeClass.getSimpleName + suffix
    //println(s"Looking for $newResource")
    val newLocation = getClass.getResource(newResource)
    //println(s"Expected location: $newLocation")
    newLocation match {
      case null => throw new Error(s"Expected to find file named $newResource in $resourcePath for class ${m.runtimeClass.getName}")
      case url: java.net.URL => url
    }
  }

  def resource[T]()(implicit m: ClassTag[T]):(String,String) = {
    (m.runtimeClass.getSimpleName, m.runtimeClass.getName)
  }

  def getLexiconProvider(prefix:String="/models/lexicon/",suffix:String=".txt"):LexiconsProvider = new LexiconsProvider {
    lazy val lexiconRoot:String = getClass.getResource(prefix).toString

    implicit def provide[C:ClassTag]: ModelProvider[C] = new ModelProvider[C] {
      val (a,b) = ModelLocator.resource[C]()
      val className = b
      val filename =  a.zipWithIndex.flatMap {  // Lexicon file names are not the same as class names
          case (u, 0) => u.toLower.toString     // FirstPerson becomes person-first.txt
          case (u, _) if u.isUpper => "-" + u.toLower
          case (l, _) => l.toString
        }.mkString("") + ".txt"

      val Iesl = "(.*\\.iesl\\..*)".r
      val Ssdi = "(.*\\.ssdi\\..*)".r
      val Uscensus = "(.*\\.uscensus\\..*)".r
      val Wikipedia = "(.*\\.wikipedia\\..*)".r
      val resource = className match {
        case Iesl(_) => prefix + "iesl/" + filename
        case Ssdi(_) => prefix + "ssdi/" + filename
        case Uscensus(_) => prefix + "uscensus/" + filename
        case Wikipedia(_) => prefix + "wikipedia/" + filename
        case _ => prefix + filename
      }

      val finalUrl:Option[URL] = getClass.getResource(resource) match {
        case url: URL => Some(url)
        case _ => {
          logger.error(s"Expected to find file named $resource in $resourcePath for class $className")
          None
        }
      }
      val coordinates: String = finalUrl.getOrElse(new URL("/")).toString
      val provide: InputStream = buffered(finalUrl.getOrElse(new URL("/")))
    }
  }


  val wordNetStreamFactory:String=>InputStream = (file:String) => getClass.getResourceAsStream("/models/wordnet/"+file)

  object StaticLexicons extends StaticLexicons()(ModelLocator.getLexiconProvider())

  object LexiconFeatures {
    def apply(lang: String = "en"): StaticLexiconFeatures = {
      new StaticLexiconFeatures(ModelLocator.StaticLexicons, lang)
    }
  }



//  def locateLexicon[C](prefix:String="/models/lexicon/",suffix:String=".txt")(implicit m: ClassTag[C]): LexiconsProvider = {
//    val filename = m.runtimeClass.getSimpleName.toLowerCase + suffix
//    val className = m.runtimeClass.getName
//    val Iesl = "(.*\\.iesl\\..*)".r
//    val Ssdi = "(.*\\.ssdi\\..*)".r
//    val Uscensus = "(.*\\.uscensus\\..*)".r
//    val Wikipedia = "(.*\\.wikipedia\\..*)".r
//    val resource = className match {
//      case Iesl(_) => prefix + "iesl/" + filename
//      case Ssdi(_) => prefix + "ssdi/" + filename
//      case Uscensus(_) => prefix + "uscensus/" + filename
//      case Wikipedia(_) => prefix + "wikipedia/" + filename
//      case _ => prefix + filename
//    }
//
//    //println(s"Looking for $newResource")
//    val newLocation = getClass.getResource(resource)
//    //println(s"Expected location: $newLocation")
//    newLocation match {
//      case null => throw new Error(s"Expected to find file named $resource in $resourcePath for class ${m.runtimeClass.getName}")
//      case url: java.net.URL => customLP[C](url)
//    }
//  }
//
//  def customLP(url:URL) = new LexiconsProvider {
//    lazy val lexiconRoot = url.toString
//    implicit def provide[C:ClassTag]: ModelProvider[C] = new ModelProvider[C] {
//
//      val provide: InputStream = buffered(url)
//      val coordinates: String = url.toString
//    }
//  }

//  val lexiconStreamFactory = (file:String) => this.getClass.getResourceAsStream("/models/ner/"+file)
//
//  val staticLexicons:StaticLexicons = new StaticLexicons()(lexiconsProvider)
//
//  private val lexiconsProvider:LexiconsProvider = new LexiconsProvider {
//    def lexiconRoot = "classpath"
//    implicit def provide[L: ClassTag]: ModelProvider[L] = new ModelProvider[L] {
//      private def url = "/models/lexicon/" + LexiconsProvider.shortLexiconName[L]
//      def coordinates: String = url
//      def provide: InputStream = this.getClass.getResourceAsStream(url)
//    }
//  }




  /* From factorie.util.ClasspathURL
  def apply[C](suffix:String)(implicit m: ClassTag[C]): java.net.URL = {
    Option(System.getProperty(m.runtimeClass.getName)) match {
      case Some(url) => try { new java.net.URL(url) }
      catch {
        case t: java.net.MalformedURLException => throw new Error(s"System property ${m.runtimeClass.getName} contains malformed url ${url+suffix}. Either fix the URL or unset the system property to open a file from the classpath.", t)
      }
      case None => m.runtimeClass.getResource(m.runtimeClass.getSimpleName+suffix) match {
        case null => throw new Error(s"No file named ${m.runtimeClass.getSimpleName + suffix} found in classpath for class ${m.runtimeClass.getName}, and no value found in system property ${m.runtimeClass.getName}. To fix this either add a file with the right name to the classpath or set the system property to point to a directory containing the file.")
        case a: java.net.URL => a
      }
    }
  } */

}

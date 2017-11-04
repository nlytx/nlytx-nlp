package cc.factorie.nlp.lexicon

import java.io.{File, InputStream}
import java.net.URL
import java.nio.file.Path

import cc.factorie.util.{ClasspathURL, ModelProvider}

import scala.reflect.{ClassTag, classTag}
import scala.util.Try

trait LexiconsProvider {
  def lexiconRoot:String
  implicit def provide[L : ClassTag]:ModelProvider[L]
}

object LexiconsProvider {
  import cc.factorie.util.ISAble._

  private def lexiconNamePieces[L:ClassTag]:Seq[String] = {
    val arr = classTag[L].runtimeClass.getName.split("""\.""").map(_.stripSuffix("$"))
    val fileName = arr.last.zipWithIndex.flatMap {
      case (u, 0) => u.toLower.toString
      case (u, _) if u.isUpper => "-" + u.toLower
      case (l, _) => l.toString
    }.mkString("") + ".txt"
    arr.init.map(_.toLowerCase) ++ Seq(fileName)
  }

  private def fullLexiconName[L:ClassTag] = lexiconNamePieces[L].mkString("/")
  def shortLexiconName[L:ClassTag] = lexiconNamePieces[L].drop(5).mkString("/")


  def fromFile(f:File, useFullPath:Boolean = false):LexiconsProvider = new LexiconsProvider {
    lazy val lexiconRoot = f.getAbsolutePath
    override implicit def provide[L : ClassTag]: ModelProvider[L] = new ModelProvider[L] {
      private val path = f.toPath.resolve(if(useFullPath) fullLexiconName[L] else shortLexiconName[L])
      val coordinates = path.toString
      val provide:InputStream = buffered(path)
    }
  }

  def fromUrl(u:URL, useFullPath:Boolean = false):LexiconsProvider = new LexiconsProvider {
    lazy val lexiconRoot = u.toString
    implicit def provide[L:ClassTag]: ModelProvider[L] = new ModelProvider[L] {
      private val modelUrl = new URL(u, if(useFullPath) fullLexiconName[L] else shortLexiconName[L])
      val provide: InputStream = buffered(modelUrl)
      val coordinates: String = modelUrl.toString
    }
  }

  implicit def providePath(p:Path):LexiconsProvider = fromFile(p.toFile, false)
  implicit def provideFile(f:File):LexiconsProvider = fromFile(f,false)
  implicit def provideURL(u:URL):LexiconsProvider = fromUrl(u, false)

  def fromString(s:String, useFullPath:Boolean=false):LexiconsProvider = s match {
    case cp if cp.toLowerCase == "classpath" => classpath(useFullPath)
    case urlS if Try(new URL(urlS)).isSuccess => fromUrl(new URL(urlS), useFullPath)
    case p => fromFile(new File(p), useFullPath)
  }

  @deprecated("This exists to preserve legacy functionality", "10/27/15")
  def classpath(useFullPath:Boolean=true):LexiconsProvider = new LexiconsProvider {
    def lexiconRoot = "classpath"
    implicit def provide[L: ClassTag]: ModelProvider[L] = new ModelProvider[L] {
      private def url = if(useFullPath) ClasspathURL.fromDirectory[Lexicon](shortLexiconName[L]) else this.getClass.getResource("/" + shortLexiconName[L])
      def coordinates: String = url.toString
      def provide: InputStream = url
    }
  }

  /*
  @deprecated("This exists to preserve legacy functionality", "10/05/15")
  def classpath:LexiconsProvider = new LexiconsProvider {
    //lazy val lexiconRoot = ClasspathURL.fromDirectory[Lexicon]("")
    lazy val lexiconRoot = Lexicon.getClass.getResource("")
    implicit def provide[L : ClassTag]: ModelProvider[L] = new ModelProvider[L] {
      private val url = {
        println("root " + lexiconRoot)
        println("shortname" + shortLexiconName[L])
        new URL(lexiconRoot, shortLexiconName[L])
      }
      val coordinates: String = url.toString
      val provide: InputStream = buffered(url)
    }
  }
  */
}
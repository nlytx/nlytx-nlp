package cc.factorie.nlp.ner

import cc.factorie.nlp.{Section, TokenSpan}

/**
  * Created by andrew@andrewresearch.net on 28/10/17.
  */

/** A TokenSpan covering a named entity.  Its entity type is indicated by its "label" member.
    @author Andrew McCallum */
abstract class NerSpan(section:Section, start:Int, length:Int) extends TokenSpan(section, start, length) {
  def label: NerSpanLabel
  override def toString = "NerSpan("+length+","+label.categoryValue+":"+this.string+")"
}

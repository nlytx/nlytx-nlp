package cc.factorie.nlp.coref

import cc.factorie.nlp.Document
import cc.factorie.nlp.phrase.{NounPhraseType, NounPhraseTypeDomain}
import cc.factorie.nlp.pos.PennPosDomain

/** An entity whose evidence comes from some Phrases within a single document.
  * Users should not create these themselves, but rather use WithinDocCoref create them.
  * The uniqueId is abstract.
  *
  * @author Andrew McCallum */
abstract class WithinDocEntity(val document:Document) extends AbstractEntity {
  type ParentType = WithinDocEntity
  private val _mentions = new scala.collection.mutable.LinkedHashSet[Mention]
  def parent: WithinDocEntity = null
  def mentions:scala.collection.Set[Mention] = _mentions
  def isSingleton:Boolean = _mentions.size == 1
  def isEmpty:Boolean = _mentions.isEmpty
  def children: Iterable[Mention] = _mentions
  // TODO Rename this to remove the "get".
  def getFirstMention: Mention = if(isEmpty) null else if(isSingleton) _mentions.head else mentions.minBy(m => m.phrase.start)
  def +=(mention:Mention): Unit = {
    assert(mention.phrase.document eq document)
    //assert(!_mentions.contains(mention)) // No reason to do this; might catch a bug.
    if (mention.entity ne null) mention.entity._mentions -= mention
    if(!_mentions.contains(mention))_mentions += mention
    mention._setEntity(WithinDocEntity.this)
  }
  def -=(mention:Mention): Unit = {
    assert(mention.phrase.document eq document)
    assert(_mentions.contains(mention)) // No reason to do this; might catch a bug.
    assert(mention.entity == this)
    _mentions -= mention
    mention._setEntity(null)
  }

  /** Return the canonical mention for the entity cluster.  If the canonical mention is not already set it computes, sets, and returns the canonical mention */
  def getCanonicalMention: Mention = {
    if (canonicalMention eq null) {
      val canonicalOption = _mentions.filter{m =>
        (m.phrase.attr[NounPhraseType].value == NounPhraseTypeDomain.value("NOM") ||
          m.phrase.attr[NounPhraseType].value == NounPhraseTypeDomain.value("NAM")) &&
          m.phrase.last.posTag.intValue != PennPosDomain.posIndex
      }.toSeq.sortBy(m => (m.phrase.start, m.phrase.length)).headOption
      canonicalMention = canonicalOption.getOrElse(children.headOption.orNull)
      canonicalName = canonicalMention.string
    }
    canonicalMention
  }
  var canonicalName: String = null
  var canonicalMention: Mention = null
  // If number, gender and entity type are needed, put a CategoricalVariable subclass in the Attr
}

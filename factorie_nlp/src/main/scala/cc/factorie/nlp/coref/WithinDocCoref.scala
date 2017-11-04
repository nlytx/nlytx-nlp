package cc.factorie.nlp.coref

import cc.factorie._
import cc.factorie.nlp.phrase.Phrase
import cc.factorie.nlp.{Document, Section, Token, TokenSpan}
import cc.factorie.util.EvaluatableClustering
import cc.factorie.variable.Span


/** Container for a within-document coreference solution, typically stored as an attr of the Document.
  * Some may contain an imperfect inferred coref solution; others may store a gold-standard target coref solution.
  * Concrete instances of Mention and WithinDocEntity are created here.
  *
  * @author Andrew McCallum
  */
class WithinDocCoref(val document:Document) extends EvaluatableClustering[WithinDocEntity,Phrase#Value] {
  /** When we have labeled gold-standard truth for coref, it is stored here. */
  var target: WithinDocCoref = null // ...the alternative would have been to create different subclasses of WithinDocCoref so they could be stored separately in the Document.attr, but I chose this as cleaner. -akm
  /** A mapping from (the Phrase's span value) to Mention */
  private val _spanToMention = new scala.collection.mutable.LinkedHashMap[Span[Section,Token],Mention]
  //private val _phraseToMention = new scala.collection.mutable.LinkedHashMap[Phrase,Mention] // Used to index by this instead.  I think we can remove this now. -akm
  /** A mapping from entity.uniqueId to WithinDocEntity */
  private val _entities = new scala.collection.mutable.LinkedHashMap[String,WithinDocEntity]
  /** A mapping from entity key (i.e. an Int identifying the true entity) to the entity.uniqueId */
  private lazy val _entityKeyToId = new scala.collection.mutable.HashMap[Int,String]
  private var _entityCount = 0 // The number of WithinDocEntities ever created here.  This number never goes down.
  /** A string that will be used as a prefix on the uniqueIds of the Mentions and WithinDocEntities created here. */
  def uniqueId: String = document.uniqueId // TODO Perhaps this should be something more safely unique if we save more than one WithinDocCoref objects per Document? -akm
  def uniqueIdEntitySuffix(entityIndex:Int): String = "//WithinDocEntity" + entityIndex
  def uniqueIdMentionSuffix(phraseStart:Int, phraseLength:Int): String = "//Mention(" + phraseStart + "," + phraseLength + ")"
  /** Concrete implementation of WithinDocEntity that automatically stores itself in WithinDocCoref.entities. */
  protected class WithinDocEntity1(val uniqueId:String) extends WithinDocEntity(document) {
    def this() = this(WithinDocCoref.this.uniqueId + uniqueIdEntitySuffix(_entityCount)) // TODO Is this what we want? -akm
    _entityCount += 1
    assert(!_entities.contains(uniqueId))
    _entities(uniqueId) = this
    def coref: WithinDocCoref = WithinDocCoref.this
  }
  /** Concrete implementation of Mention that automatically stores itself in WithinDocCoref.mentions. */
  protected class Mention1(phrase:Phrase, entity:WithinDocEntity) extends Mention(phrase) {
    def this(phrase:Phrase, entityKey:Int) = this(phrase, entityFromKey(entityKey)) // Typically used for labeled data
    def this(phrase:Phrase, entityUniqueId:String) = this(phrase, entityFromUniqueId(entityUniqueId)) // Typically used for deserialization
    def this(phrase:Phrase) = this(phrase, null.asInstanceOf[WithinDocEntity]) // Typically used for new inference // TODO Should this be null, or a newly created blank Entity; See LoadConll2011 also.
    assert(entity == null || entity.asInstanceOf[WithinDocEntity1].coref == WithinDocCoref.this)
    _spanToMention(phrase.value) = this
    val uniqueId = WithinDocCoref.this.uniqueId + uniqueIdMentionSuffix(phrase.start, phrase.length) // TODO Is this what we want? -akm
    if (entity ne null) entity += this
    def coref: WithinDocCoref = WithinDocCoref.this
  }

  /** Given Span (typically the value of a Phrase), return the corresponding Mention.
      Note that Span is a case class, so the lookup is done by the span's boundaries, not by its identity. */
  def mention(span:Span[Section,Token]): Option[Mention] = _spanToMention.get(span)
  /** Return the Mention corresponding to the given Phrase.  If none present, return null.
      Note that since the lookup happens by the Phrase's Span value, the returned mention.phrase may be different than this method's argument. */
  def mention(phrase:Phrase): Option[Mention] = _spanToMention.get(phrase.value)

  /** Create a new Mention whose entity will be null. */
  def addMention(phrase:Phrase): Mention = _spanToMention.getOrElse(phrase.value, new Mention1(phrase))
  /** Create a new Mention with entity specified by given uniqueId. */
  def addMention(phrase:Phrase, entityId:String): Mention = { assert(!_spanToMention.contains(phrase.value)); new Mention1(phrase, entityId) }
  /** Create a new Mention with entity specified by given key. */
  def addMention(phrase:Phrase, entityKey:Int): Mention = { assert(!_spanToMention.contains(phrase.value)); new Mention1(phrase, entityKey) }
  /** Create a new Mention with the given entity, which must also be in this WithinDocCoref */
  def addMention(phrase:Phrase, entity:WithinDocEntity): Mention = new Mention1(phrase, entity)

  /** Remove a Mention from this coreference solution, and from its entity if it has one. */
  def deleteMention(mention:Mention): Unit = {
    if (mention.entity ne null) mention.entity -= mention
    _spanToMention.remove(mention.phrase.value)
  }

  /** Checks whether the given tokenspan overlaps with an existing mention, returns the overlapping mention if it does. */
  def findOverlapping(tokenSpan:TokenSpan):Option[Mention] = tokenSpan match {
    case ts if ts.document == this.document => mentions.find(_.phrase.characterOffsets overlapsWith ts.characterOffsets)
    case _ => None
  }

  /** Return all Mentions in this coreference solution. */
  def mentions: Seq[Mention] = _spanToMention.values.toVector
  /** Return a collection of WithinDocEntities managed by this coref solution.  Note that some of them may have no Mentions. */
  def entities: Iterable[WithinDocEntity] = _entities.values
  /** Create and return a new WithinDocEntity with uniqueId determined by the number entities created so far. */
  def newEntity(): WithinDocEntity = new WithinDocEntity1()
  /** Return the entity associated with the given uniqueId, or create a new entity if not found already among 'entities'. */
  def entityFromUniqueId(id:String): WithinDocEntity = _entities.getOrElse(id, new WithinDocEntity1(id))
  /** Return the entity associated with the given key, or create a new entity if not found already among 'entities'. */
  def entityFromKey(key:Int): WithinDocEntity = {
    val id = _entityKeyToId.getOrElse(key,null)
    val result = if (id eq null) new WithinDocEntity1 else _entities(id)
    _entityKeyToId(key) = result.uniqueId
    result
  }
  /** Return the entity associated with the given uniqueId.  Return null if not found. */
  def idToEntity(id:String): WithinDocEntity = _entities(id)
  /** Remove from the list of entities all entities that contain no mentions. */
  def trimEmptyEntities(): Unit = _entities.values.filter(_.mentions.size == 0).map(_.uniqueId).foreach(_entities.remove) // TODO But note that this doesn't purge _entityKeyToId; perhaps it should.
  /** Remove from all entities and mentions associated with entities that contain only one mention. */
  def removeSingletons():Unit ={
    _entities.values.filter(_.mentions.size == 1).map(_.uniqueId).foreach{
      id =>
        _entities(id).mentions.foreach(m => deleteMention(m))
        _entities.remove(id)
    }
  }

  /**Reset the clustered entities for this coref solution without losing mentions and their cached properties*/
  def resetPredictedMapping():Unit = {_entities.clear();mentions.foreach(_._setEntity(null));_entityCount = 0 }

  // Support for evaluation
  // These assure we ignore any singletons for conll scoring
  // TODO: Allow for ACE scoring where singletons are counted
  def clusterIds: Iterable[WithinDocEntity] = _entities.values.filterNot(_.isSingleton)
  def pointIds: Iterable[Phrase#Value] = _spanToMention.values.filterNot(m => m.entity == null || m.entity.isSingleton).map(_.phrase.value)
  def pointIds(entityId:WithinDocEntity): Iterable[Phrase#Value] = if(!entityId.isSingleton) entityId.mentions.map(_.phrase.value) else Seq()
  def intersectionSize(entityId1:WithinDocEntity, entityId2:WithinDocEntity): Int = if(!entityId1.isSingleton && !entityId2.isSingleton) entityId1.mentions.map(_.phrase.value).intersect(entityId2.mentions.map(_.phrase.value)).size else 0
  def clusterId(mentionId:Phrase#Value): WithinDocEntity = {
    val mention = _spanToMention.getOrElse(mentionId,null)
    if(mention == null || mention.entity == null ||mention.entity.isSingleton) null
    else mention.entity
  }


}
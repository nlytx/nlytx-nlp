package cc.factorie.nlp.coref

import cc.factorie.nlp.phrase.Phrase


// TODO Turn this into a trait.  Only concrete will be an inner class of WithinDocCoref
/** An entity mention whose contents come from a nlp.phrase.Phrase.
    Users should not create these themselves, but rather use WithinDocCoref create them.
    The uniqueId is abstract.
    @author Andrew McCallum */
abstract class Mention(val phrase:Phrase) extends AbstractMention {
  type ParentType = WithinDocEntity
  private var _entity:WithinDocEntity = null
  protected[coref] def _setEntity(e:WithinDocEntity): Unit = _entity = e
  def entity: ParentType = _entity
  def parent: ParentType = _entity
  lazy val string = phrase.tokensString(" ")
  // If number, gender and entity type are needed, put a CategoricalVariable subclass in the Attr
}
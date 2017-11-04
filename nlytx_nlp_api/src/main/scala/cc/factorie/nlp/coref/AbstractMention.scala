package cc.factorie.nlp.coref

/** A "mention" of an entity in a resolution problem.
  * A leaf in a coreference hierarchy.
  * This is the super-trait for mentions in both within-document coreference and cross-document entity resolution.
  *
  * @author Andrew McCallum */
trait AbstractMention extends Node {
  def parent: ParentType
  /** The root of the coreference tree in which this mention is a leaf. */
  def entity: ParentType
  /** A string representation of the observed mention, e.g. "Michael Smith". */
  def string: String
}
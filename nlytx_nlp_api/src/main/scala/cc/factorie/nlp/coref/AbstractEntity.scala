package cc.factorie.nlp.coref

/** An "entity" in an entity resolution problem.
  * A non-leaf Node in a coreference hierarchy.
  * It could be a root (entity) or an intermediate node (sub-entity in hierarchical coref).
  * This is the super-trait for entities in both within-document coreference and cross-document entity resolution.
  *
  * @author Andrew McCallum */
trait AbstractEntity extends Node {
  def children: Iterable[Node]  // Immediate children
  def childIds: Iterable[String] = children.map(_.uniqueId)
  def mentions: Iterable[AbstractMention] // Leaves of tree
}
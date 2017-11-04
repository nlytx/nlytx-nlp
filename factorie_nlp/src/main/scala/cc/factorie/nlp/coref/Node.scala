package cc.factorie.nlp.coref

import cc.factorie.util.{Attr, UniqueId}

/** Either a mention, entity or sub-entity in an coreference or entity resolution model.
  * These are the "nodes" in a trees in which observed mentions are the leaves and inferred entities are the roots.
  * In "hierarchical coreference" there may be additional nodes at intermediate levels of the tree.
  *
  * @author Andrew McCallum */
trait Node extends UniqueId with Attr {
  type ParentType <: Node
  /** A pointer to the Node immediate above this Node in the tree. */
  def parent: ParentType
}

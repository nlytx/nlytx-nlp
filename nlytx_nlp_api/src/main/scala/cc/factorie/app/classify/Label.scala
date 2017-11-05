package cc.factorie.app.classify


import cc.factorie.variable._


/**
  * Created by andrew@andrewresearch.net on 24/10/17.
  */

// Feature and Label classes

class Label(val labelName: String, val features: Features, val domain: CategoricalDomain[String]) extends LabeledCategoricalVariable(labelName) {
  override def toString = "instance=%s label=%s" format (features.instanceName, categoryValue)
  override val skipNonCategories = true
}
trait Features extends VectorVar {
  this: CategoricalVectorVariable[String] =>
  def labelName: String
  def instanceName: String
  def domain: CategoricalVectorDomain[String]
  def labelDomain: CategoricalDomain[String]
  var label = new Label(labelName, this, labelDomain)
}
class BinaryFeatures(val labelName: String, val instanceName: String, val domain: CategoricalVectorDomain[String], val labelDomain: CategoricalDomain[String])
  extends BinaryFeatureVectorVariable[String] with Features { override val skipNonCategories = true }
class NonBinaryFeatures(val labelName: String, val instanceName: String, val domain: CategoricalVectorDomain[String], val labelDomain: CategoricalDomain[String])
  extends FeatureVectorVariable[String] with Features { override val skipNonCategories = true }

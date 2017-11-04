package cc.factorie.nlp.pos

import cc.factorie.nlp.Token
import cc.factorie.variable.CategoricalVariable

/**
  * Created by andrew@andrewresearch.net on 27/10/17.
  */

abstract class PosTag(val token:Token, initialIndex:Int) extends CategoricalVariable[String](initialIndex)
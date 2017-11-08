package io.nlytx.commons

import org.scalatest._

/**
  * Created by andrew@andrewresearch.net on 14/11/16.
  */

abstract class UnitSpec extends FlatSpec with Matchers with
  OptionValues with Inside with Inspectors

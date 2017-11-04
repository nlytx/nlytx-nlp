package cc.factorie.nlp.phrase

import cc.factorie.variable.EnumDomain

object GenderDomain extends EnumDomain {
  val UNKNOWN,     // uncertain
  NEUTER,          // known to be non-person
  PERSON,          // person, but uncertain about gender
  MALE,            // male person
  FEMALE = Value   // female person
  freeze()
}

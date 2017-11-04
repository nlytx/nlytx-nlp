/* Copyright (C) 2008-2016 University of Massachusetts Amherst.
   This file is part of "FACTORIE" (Factor graphs, Imperative, Extensible)
   http://factorie.cs.umass.edu, http://github.com/factorie
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package cc.factorie.nlp.lexicon.ssdi

import cc.factorie.nlp.lexicon.ProvidedTriePhraseLexicon
import cc.factorie.util.ModelProvider

/**
 * @author johnsullivan
 */



  class PersonFirstHighest()(implicit mp: ModelProvider[PersonFirstHighest]) extends ProvidedTriePhraseLexicon[PersonFirstHighest]

  class PersonFirstHigh()(implicit mp: ModelProvider[PersonFirstHigh]) extends ProvidedTriePhraseLexicon[PersonFirstHigh]

  class PersonFirstMedium()(implicit mp: ModelProvider[PersonFirstMedium]) extends ProvidedTriePhraseLexicon[PersonFirstMedium]

  class PersonLastHighest()(implicit mp: ModelProvider[PersonLastHighest]) extends ProvidedTriePhraseLexicon[PersonLastHighest]

  class PersonLastHigh()(implicit mp: ModelProvider[PersonLastHigh]) extends ProvidedTriePhraseLexicon[PersonLastHigh]

  class PersonLastMedium()(implicit mp: ModelProvider[PersonLastMedium]) extends ProvidedTriePhraseLexicon[PersonLastMedium]

package cc.factorie.nlp.lexicon

import cc.factorie.nlp.lemma.LowercaseLemmatizer
import cc.factorie.app.strings.nonWhitespaceClassesSegmenter

/** A non-exhaustive list of common English prepositions. */
object Preposition extends PhraseLexicon("Preposition", nonWhitespaceClassesSegmenter, LowercaseLemmatizer) {
  this ++=
    """about
above
across
after
against
around
at
before
behind
below
beneath
beside
besides
between
beyond
by
down
during
except
for
from
in
inside
into
like
near
of
off
on
out
outside
over
since
through
throughout
till
to
toward
under
until
up
upon
with
without"""
}
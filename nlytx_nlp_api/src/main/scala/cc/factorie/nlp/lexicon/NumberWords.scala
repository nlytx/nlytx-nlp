package cc.factorie.nlp.lexicon

import cc.factorie.nlp.lemma.LowercaseLemmatizer

import cc.factorie.app.strings.nonWhitespaceClassesSegmenter

/**
  * Created by andrew@andrewresearch.net on 28/10/17.
  */

object NumberWords extends PhraseLexicon("NumberWords", nonWhitespaceClassesSegmenter, LowercaseLemmatizer) {
  this ++=
    """zero
one
two
three
four
five
six
seven
eight
nine
ten
tens
dozen
dozens
eleven
twelve
thirteen
fourteen
fifteen
sixteen
seventeen
eighteen
nineteen
twenty
thirty
forty
fifty
sixty
seventy
eighty
ninety
hundred
hundreds
thousand
thousands
million
millions
billion
billions
trillion
trillions
quadrillion
quintillion
sextillion
septillion
zillion
umpteen
multimillion
multibillion
"""
}

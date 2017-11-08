package io.nlytx.nlp.annotators

import cc.factorie.nlp.segment.DeterministicLexerTokenizer

/**
  * Created by andrew@andrewresearch.net on 5/11/17.
  */
//Equivalent to DeterministicNormalizingTokenizer
object Tokeniser extends DeterministicLexerTokenizer(
  tokenizeSgml = false,
  tokenizeNewline = false,
  tokenizeWhitespace = false,
  tokenizeAllDashedWords = false,
  abbrevPrecedesLowercase = false,
  normalize = true,
  normalizeQuote = true,
  normalizeApostrophe = true,
  normalizeCurrency = true,
  normalizeAmpersand = true,
  normalizeFractions = true,
  normalizeEllipsis = true,
  undoPennParens = true,
  unescapeSlash = true,
  unescapeAsterisk = true,
  normalizeMDash = true,
  normalizeDash = true,
  normalizeHtmlSymbol = true,
  normalizeHtmlAccent = true
)

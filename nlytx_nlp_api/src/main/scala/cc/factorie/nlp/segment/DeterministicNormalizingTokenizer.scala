package cc.factorie.nlp.segment

/* This token performs normalization while it tokenizes, removing html tags; You probably want to use this one */

object DeterministicNormalizingTokenizer extends DeterministicLexerTokenizer(
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

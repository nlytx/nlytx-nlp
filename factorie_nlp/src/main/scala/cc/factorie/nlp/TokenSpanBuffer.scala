package cc.factorie.app.nlp

import cc.factorie.nlp.{Section, Token, TokenSpan}
import cc.factorie.variable.SpanVarBuffer

/** A mutable collection of TokenSpans, with various methods to returns filtered sub-sets of spans based on position and class. */
class TokenSpanBuffer[S<:TokenSpan] extends SpanVarBuffer[S, Section, Token] with TokenSpanCollection[S] with Serializable
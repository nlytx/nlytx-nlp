package cc.factorie.app.nlp

import cc.factorie.nlp.{Section, Token, TokenSpan}
import cc.factorie.variable.SpanVarCollection

trait TokenSpanCollection[S<:TokenSpan] extends SpanVarCollection[S, Section, Token]
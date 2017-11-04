package cc.factorie.nlp.ner

import cc.factorie.app.nlp.TokenSpanBuffer


trait NerSpanBuffer[Tag <: NerSpan] extends TokenSpanBuffer[Tag]


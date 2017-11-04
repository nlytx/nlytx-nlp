package cc.factorie.nlp

import cc.factorie.nlp.ner.NerTag
import cc.factorie.nlp.parse.ParseTree
import cc.factorie.nlp.pos.PennPosTag

/**
  * Created by andrew@andrewresearch.net on 27/10/17.
  */

/** A span of Tokens making up a sentence within a Section of a Document.
    A Sentence is a special case of a TokenSpan, stored in its Section, and available through the Section.sentences method.
    From the Sentence you can get its sequence of Tokens, the Section that contains it, and the Document that contains it.
    Sentences can be added (in order) to a Section, but not removed from a Section.
    The index of this Sentence into the sequence of Sentences in the Section is available as 'Sentence.indexInSection'.
    The annotation ParseTree is stored on a Sentence.
    Unlike other TokenSpans, constructing a Sentence automatically add it to its Sections.
    @author Andrew McCallum */
class Sentence(sec:Section, initialStart:Int, initialLength:Int)
  extends TokenSpan(sec, initialStart, initialLength) with Serializable {
  /** Construct a new 0-length Sentence that begins just past the current last token of the Section, and add it to the Section automatically.
      This constructor is typically used when reading labeled training data one token at a time, where we need Sentence and Token objects. */
  def this(sec:Section) = this(sec, sec.length, 0)
  /** Construct a new 0-length Sentence that begins just past the current last token of the doc.asSection, and add it to the Section automatically.
      This constructor is typically used when reading labeled training data one token at a time, where we need Sentence and Token objects. */
  def this(doc:Document) = this(doc.asSection)

  // Initialization
  // removed for efficiency -- shouldn't we do this in the annotators / loaders ?
  //  if (!sec.document.annotators.contains(classOf[Sentence])) sec.document.annotators(classOf[Sentence]) = UnknownDocumentAnnotator.getClass
  sec.addSentence(this)
  private val _indexInSection: Int = sec.sentences.length - 1

  /** Returns the number of Sentences before this one in the Section. */
  def indexInSection: Int = _indexInSection

  /** Returns true if the given Token is inside this Sentence. */
  def contains(element:Token) = tokens.contains(element) // TODO Re-implement this to be faster avoiding search using token.stringStart bounds

  // Parse attributes
  /** If this Sentence has a ParseTree, return it; otherwise return null. */
  def parse = attr[ParseTree]
  /** Return the Token at the root of this Sentence's ParseTree. Will throw an exception if there is no ParseTree. */
  def parseRootChild: Token = attr[ParseTree].rootChild

  // common labels
  /** Returns the sequence of PennPosTags attributed to the sequence of Tokens in this Sentence. */
  def posTags: IndexedSeq[PennPosTag] = tokens.map(_.attr[PennPosTag])
  /** Returns the sequence of NerTags attributed to the sequence of Tokens in this Sentence. */
  def nerTags: IndexedSeq[NerTag] = tokens.map(_.nerTag)
}

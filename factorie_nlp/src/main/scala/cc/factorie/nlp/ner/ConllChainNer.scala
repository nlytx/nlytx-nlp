package cc.factorie.nlp.ner

import java.io.Serializable

import cc.factorie.nlp.Section
import cc.factorie.util.ModelProvider


/**
  * NER tagger for the CoNLL 2003 corpus
  *
  * Training time: ~3 minutes (on blake, 30 Oct. 4:00pm)
  * tokens per second: 8431.02310444517
  * docs per second: 48.24287793720109 (avg doc length = 200 tokens)
  *
  * CoNLL 2003 dev set (eng.testa)
  * OVERALL  f1=0.933593 p=0.939802 r=0.927465 (tp=5511 fp=353 fn=431 true=5942 pred=5864) acc=0.985865 (50636/51362)
  * LOC      f1=0.965931 p=0.967249 r=0.964616 (tp=1772 fp=60 fn=65 true=1837 pred=1832)
  * MISC     f1=0.876404 p=0.909091 r=0.845987 (tp=780 fp=78 fn=142 true=922 pred=858)
  * ORG      f1=0.892065 p=0.899848 r=0.884415 (tp=1186 fp=132 fn=155 true=1341 pred=1318)
  * PER      f1=0.958897 p=0.955280 r=0.962541 (tp=1773 fp=83 fn=69 true=1842 pred=1856)
  *
  * CoNLL 2003 test set (eng.testb)
  * OVERALL  f1=0.885633 p=0.888315 r=0.882967 (tp=4987 fp=627 fn=661 true=5648 pred=5614) acc=0.973253 (45193/46435)
  * LOC      f1=0.915375 p=0.909953 r=0.920863 (tp=1536 fp=152 fn=132 true=1668 pred=1688)
  * MISC     f1=0.791034 p=0.803231 r=0.779202 (tp=547 fp=134 fn=155 true=702 pred=681)
  * ORG      f1=0.842767 p=0.838498 r=0.847080 (tp=1407 fp=271 fn=254 true=1661 pred=1678)
  * PER      f1=0.940327 p=0.955329 r=0.925788 (tp=1497 fp=70 fn=120 true=1617 pred=1567)
  *
  */
class ConllChainNer(implicit mp:ModelProvider[ConllChainNer], nerLexiconFeatures:NerLexiconFeatures)
  extends ChainNer[BilouConllNerTag](
    BilouConllNerDomain,
    (t, s) => new BilouConllNerTag(t, s),
    l => l.token,
    mp.provide,
    nerLexiconFeatures) with Serializable {

  //TODO Only used for training - I think!
  //def loadDocs(fileName: String): Seq[Document] = LoadConll2003(BILOU=true).fromFilename(fileName)

  def newSpan(sec: Section, start: Int, length: Int, category: String) = new ConllNerSpan(sec, start, length, category)

  def newBuffer = new ConllNerSpanBuffer
}

//TODO this serialized model doesn't exist yet?
object ConllChainNer extends ConllChainNer()(ModelProvider.classpath(), StaticLexiconFeatures()) with Serializable

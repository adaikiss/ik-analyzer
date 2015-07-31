/**
 *
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.IOException;


/**
 * IK Analyzer v3.2
 * Lucene3.0 Tokenizer适配器类
 * 它封装了IKSegmentation实现
 *
 * @author 林良益
 */
public final class IKTokenizer extends Tokenizer {
    //IK分词器实现
    private IKSegmentation _IKImplement;
    //词元文本属性
    private CharTermAttribute termAtt;
    //词元位移属性
    private OffsetAttribute offsetAtt;
    //记录最后一个词元的结束位置
    private int finalOffset;

    /**
     * Lucene Tokenizer适配器类构造函数
     *
     * @param useSmart 当为false时，分词器进行最大词长切分；当为true是，采用最细粒度切分
     */
    public IKTokenizer(boolean useSmart) {
        init(useSmart);
    }

    private void init(boolean useSmart) {
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        _IKImplement = new IKSegmentation(input, useSmart);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        //清除所有的词元属性
        clearAttributes();
        Lexeme nextLexeme = _IKImplement.next();
        if (nextLexeme != null) {
            //将Lexeme转成Attributes
            //设置词元文本
            String lexemeText = nextLexeme.getLexemeText();
            termAtt.copyBuffer(lexemeText.toCharArray(), 0, lexemeText.length());
            //设置词元长度
            termAtt.setLength(nextLexeme.getLength());
            //设置词元位移
            offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
            //记录分词的最后位置
            finalOffset = nextLexeme.getEndPosition();
            //返会true告知还有下个词元
            return true;
        }
        //返会false告知词元输出完毕
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.lucene.analysis.Tokenizer#reset(java.io.Reader)
     */
    public void reset() throws IOException {
        super.reset();
        _IKImplement.reset(input);
    }

    @Override
    public final void end() {
        // set final offset
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

}

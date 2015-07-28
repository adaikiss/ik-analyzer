/**
 * 
 */
package org.wltea.analyzer.solr;

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.wltea.analyzer.lucene.IKTokenizer;

/**
 * 实现Solr1.4分词器接口
 * 基于IKTokenizer的实现
 * 
 * @author 林良益、李良杰
 *
 */
public final class IKTokenizerFactory extends TokenizerFactory{
	
	private boolean isMaxWordLength;
	
	/**
	 * IK分词器Solr TokenizerFactory接口实现类
	 * 默认最细粒度切分算法
	 */
	public IKTokenizerFactory(Map<String,String> args){
		super(args);
        isMaxWordLength = getBoolean(args, "isMaxWordLength", false);
	}

	@Override
	public Tokenizer create(AttributeFactory factory) {
		IKTokenizer ikTokenizer = new IKTokenizer(isMaxWordLength);
		return ikTokenizer;
	}

}

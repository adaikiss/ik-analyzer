/**
 * 
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;

/**
 * 实现Lucene Analyzer
 * 基于IKTokenizer的中文分词器
 * 
 * @author 林良益
 *
 */
public final class IKAnalyzer extends Analyzer {
	
	private boolean isMaxWordLength = false;
	
	/**
	 * IK分词器Lucene Analyzer接口实现类
	 * 默认最细粒度切分算法
	 */
	public IKAnalyzer(){
		this(false);
	}
	
	/**
	 * IK分词器Lucene Analyzer接口实现类
	 * 
	 * @param isMaxWordLength 当为true时，分词器进行最大词长切分
	 */
	public IKAnalyzer(boolean isMaxWordLength){
		super();
		this.setMaxWordLength(isMaxWordLength);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		return new TokenStreamComponents(new IKTokenizer(isMaxWordLength()));
	}

	public void setMaxWordLength(boolean isMaxWordLength) {
		this.isMaxWordLength = isMaxWordLength;
	}

	public boolean isMaxWordLength() {
		return isMaxWordLength;
	}

}

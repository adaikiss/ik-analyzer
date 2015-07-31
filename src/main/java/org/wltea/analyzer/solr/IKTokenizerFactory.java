/**
 *
 */
package org.wltea.analyzer.solr;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKTokenizer;

import java.io.IOException;
import java.util.Map;

/**
 * 实现Solr1.4分词器接口
 * 基于IKTokenizer的实现
 *
 * @author 林良益、李良杰
 */
public final class IKTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {
    private static final Logger logger = LoggerFactory.getLogger(IKTokenizerFactory.class);

    private boolean useSmart;

    /**
     * IK分词器Solr TokenizerFactory接口实现类
     * 默认最细粒度切分算法
     */
    public IKTokenizerFactory(Map<String, String> args) {
        super(args);
        useSmart = getBoolean(args, "useSmart", false);

    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        logger.info("Creating IKTokenizerFactory...");
        IKTokenizer ikTokenizer = new IKTokenizer(useSmart);
        return ikTokenizer;
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        logger.info("Loading resources...");
        Dictionary.init(loader, getOriginalArgs());
    }
}

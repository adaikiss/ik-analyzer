/**
 *
 */
package org.wltea.analyzer.dic;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * IK Analyzer v3.2
 * 词典管理类,单例模式
 *
 * TODO:由于需要加载大量词典到内存，暂时使用单例词典，即索引和查询使用同一词典实例，待优化
 *
 * @author 林良益
 */
public class Dictionary {
    private static final Logger logger = LoggerFactory.getLogger(Dictionary.class);
    /*
     * 分词器默认字典路径
     */
    public static final String PATH_DIC_MAIN = "dict/main.dic";
    public static final String PATH_DIC_SURNAME = "dict/surname.dic";
    public static final String PATH_DIC_QUANTIFIER = "dict/quantifier.dic";
    public static final String PATH_DIC_SUFFIX = "dict/suffix.dic";
    public static final String PATH_DIC_PREP = "dict/preposition.dic";
    public static final String PATH_DIC_STOP = "dict/stopword.dic";

    private String pathDictMain = PATH_DIC_MAIN;
    private String pathDictSurname = PATH_DIC_SURNAME;
    private String pathDictQuantifier = PATH_DIC_QUANTIFIER;
    private String pathDictSuffix = PATH_DIC_SUFFIX;
    private String pathDictPreposition = PATH_DIC_PREP;
    private String pathDictStopword = PATH_DIC_STOP;

    /*
     * 词典实例
     */
    private static volatile Dictionary instance = new Dictionary();

    private boolean inited = false;

    /**
     * 由于可以通过init预先初始化，所以单例不加锁
     *
     * @return
     */
    public static Dictionary getInstance() {
        return instance;
    }

    public static void init(ResourceLoader loader) throws IOException {
        init(loader, null);
    }

    public static synchronized void init(ResourceLoader loader, Map<String, String> args) throws IOException {
        instance._init(loader, args);
    }

    private void _init(ResourceLoader loader, Map<String, String> args) throws IOException {
        if(inited){
            return;
        }
        logger.info("start initializing dictionary...");
        if (args != null && args.size() > 0) {
            pathDictMain = coalesce(args.get("main"), PATH_DIC_MAIN);
            pathDictSurname = coalesce(args.get("surname"), PATH_DIC_SURNAME);
            pathDictQuantifier = coalesce(args.get("quantifier"), PATH_DIC_QUANTIFIER);
            pathDictSuffix = coalesce(args.get("suffix"), PATH_DIC_SUFFIX);
            pathDictPreposition = coalesce(args.get("prep"), PATH_DIC_PREP);
            pathDictStopword = coalesce(args.get("stop"), PATH_DIC_STOP);
        }
        //初始化系统词典
        _MainDict = loadDictSegment(loader, pathDictMain);
        _SurnameDict = loadDictSegment(loader, pathDictSurname);
        _QuantifierDict = loadDictSegment(loader, pathDictQuantifier);
        _SuffixDict = loadDictSegment(loader, pathDictSuffix);
        _PrepDict = loadDictSegment(loader, pathDictPreposition);
        _StopWords = loadDictSegment(loader, pathDictStopword);
        logger.info("finished initializing dictionary.");
        inited = true;
    }

    /*
     * 主词典对象
     */
    private DictSegment _MainDict;
    /*
     * 姓氏词典
     */
    private DictSegment _SurnameDict;
    /*
     * 量词词典
     */
    private DictSegment _QuantifierDict;
    /*
     * 后缀词典
     */
    private DictSegment _SuffixDict;
    /*
     * 副词，介词词典
     */
    private DictSegment _PrepDict;
    /*
     * 停止词集合
     */
    private DictSegment _StopWords;

    private Dictionary() {

    }


    /**
     * 检索匹配主词典
     *
     * @param charArray
     * @return Hit 匹配结果描述
     */
    public Hit matchInMainDict(char[] charArray) {
        return _MainDict.match(charArray);
    }

    /**
     * 检索匹配主词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInMainDict(char[] charArray, int begin, int length) {
        return _MainDict.match(charArray, begin, length);
    }

    /**
     * 检索匹配主词典,
     * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
     *
     * @param charArray
     * @param currentIndex
     * @param matchedHit
     * @return Hit
     */
    public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
        DictSegment ds = matchedHit.getMatchedDictSegment();
        return ds.match(charArray, currentIndex, 1, matchedHit);
    }

    /**
     * 检索匹配姓氏词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInSurnameDict(char[] charArray, int begin, int length) {
        return _SurnameDict.match(charArray, begin, length);
    }

//	/**
//	 * 
//	 * 在姓氏词典中匹配指定位置的char数组
//	 * （对传入的字串进行后缀匹配）
//	 * @param charArray
//	 * @param begin
//	 * @param end
//	 * @return
//	 */
//	public static boolean endsWithSurnameDict(char[] charArray , int begin, int length){
//		Hit hit = null;
//		for(int i = 1 ; i <= length ; i++){
//			hit = singleton._SurnameDict.match(charArray, begin + (length - i) , i);
//			if(hit.isMatch()){
//				return true;
//			}
//		}
//		return false;
//	}

    /**
     * 检索匹配量词词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInQuantifierDict(char[] charArray, int begin, int length) {
        return _QuantifierDict.match(charArray, begin, length);
    }

    /**
     * 检索匹配在后缀词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInSuffixDict(char[] charArray, int begin, int length) {
        return _SuffixDict.match(charArray, begin, length);
    }

//	/**
//	 * 在后缀词典中匹配指定位置的char数组
//	 * （对传入的字串进行前缀匹配）
//	 * @param charArray
//	 * @param begin
//	 * @param end
//	 * @return
//	 */
//	public static boolean startsWithSuffixDict(char[] charArray , int begin, int length){
//		Hit hit = null;
//		for(int i = 1 ; i <= length ; i++){
//			hit = singleton._SuffixDict.match(charArray, begin , i);
//			if(hit.isMatch()){
//				return true;
//			}else if(hit.isUnmatch()){
//				return false;
//			}
//		}
//		return false;
//	}

    /**
     * 检索匹配介词、副词词典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配结果描述
     */
    public Hit matchInPrepDict(char[] charArray, int begin, int length) {
        return _PrepDict.match(charArray, begin, length);
    }

    /**
     * 判断是否是停止词
     *
     * @param charArray
     * @param begin
     * @param length
     * @return boolean
     */
    public boolean isStopWord(char[] charArray, int begin, int length) {
        return _StopWords.match(charArray, begin, length).isMatch();
    }

    private static String coalesce(String... values) {
        for (String s : values) {
            if (s == null) {
                continue;
            }
            s = s.trim();
            if (s.length() > 0) {
                return s;
            }
        }
        return null;
    }

    /**
     * 加载词典
     *
     * @param loader
     * @param resource
     * @return
     */
    private DictSegment loadDictSegment(ResourceLoader loader, String resource) throws IOException {
        DictSegment dictSegment = new DictSegment((char) 0);
        List<String> files = splitFileNames(resource);
        if(files.size() > 0){
            for (String file : files) {
                if(file == null){
                    continue;
                }
                file = file.trim();
                if(file.length() == 0){
                    continue;
                }
                logger.info("loading dict from {}", file);
                getLines(loader.openResource(file), StandardCharsets.UTF_8, dictSegment);
            }
        }
        return dictSegment;
    }

    private void getLines(InputStream stream, Charset charset, DictSegment dictSegment) throws IOException {
        BufferedReader input = null;
        boolean success = false;
        boolean firstLine = true;
        try {
            input = (BufferedReader) IOUtils.getDecodingReader(stream, charset);
            for (String word; (word = input.readLine()) != null; ) {
                // skip initial bom marker
                if (firstLine && word.length() > 0 && word.charAt(0) == '\uFEFF')
                    word = word.substring(1);
                firstLine = false;
                // skip comments
                if (word.startsWith("#")) continue;
                word = word.trim();
                // skip blank lines
                if (word.length() == 0) continue;
                dictSegment.fillSegment(word.toCharArray());
            }
            success = true;
        } finally {
            if (success) {
                IOUtils.close(input);
            } else {
                IOUtils.closeWhileHandlingException(input);
            }
        }
    }

    protected final List<String> splitFileNames(String fileNames) {
        if (fileNames == null)
            return Collections.emptyList();

        List<String> result = new ArrayList<>();
        for (String file : fileNames.split("(?<!\\\\),")) {
            result.add(file.replaceAll("\\\\(?=,)", ""));
        }

        return result;
    }
}

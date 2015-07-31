## Introduction

IK Analyzer是一个开源的，基于java语言开发的轻量级的中文分词工具包。从2006年12月推出1.0版开始， IKAnalyzer已经推出了4个大版本。最初，它是以开源项目Luence为应用主体的，结合词典分词和文法分析算法的中文分词组件。从3.0版本开始，IK发展为面向Java的公用分词组件，独立于Lucene项目，同时提供了对Lucene的默认优化实现。在2012版本中，IK实现了简单的分词歧义排除算法，标志着IK分词器从单纯的词典分词向模拟语义分词衍化。

他的开发者是[林良益](http://linliangyi2007.iteye.com/ )，此项目fork自[killme2008/ik-analyzer](https://github.com/killme2008/ik-analyzer)，修改少量方法使其支持solr 5.x，同时修改了读取词典文件的方式。

## 协议

遵循原始协议 Apache license,Version 2.0

## 构建

    mvn package
	
## 导入IDE

如果使用eclipse:

	mvn eclipse:eclipse
	
如果使用Intelij idea:

    mvn idea:idea

## 使用

以%SOLR_HOME%表示solr安装目录，将打包后生成的jar(ik-analyzer-0.1-SNAPSHOT.jar)放到%SOLR_HOME%/dist目录下，然后在solrconfig.xml中的`<config/>`元素内添加如下节点：
```xml
<lib dir="${solr.install.dir:../../../..}/dist/" regex="ik-analyzer-.*\.jar" />
```

在schema.xml中添加类似如下的配置:
```xml
<fieldType name="text_cn" class="solr.TextField" positionIncrementGap="100" >  
	<analyzer type="index" >   
		<tokenizer class="org.wltea.analyzer.solr.IKTokenizerFactory" useSmart="true" stop="dict/stopword.dic,dict/ext_stopword.dic" main="dict/main.dic,dict/main_book_name.dic,dict/main_book_author.dic,dict/main_book_tag.dic"/>
		<filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/>
		<filter class="solr.LowerCaseFilterFactory"/>
	</analyzer>

	<analyzer type="query">
		<tokenizer class="org.wltea.analyzer.solr.IKTokenizerFactory" useSmart="true" stop="dict/stopword.dic,dict/ext_stopword.dic" main="dict/main.dic,dict/main_book_name.dic,dict/main_book_author.dic,dict/main_book_tag.dic"/>
	</analyzer>
</fieldType>
```

最后将本项目`src\main\assets`中的dict目录复制到core/conf所在的目录，即solrconfig.xml与schema.xml所在的目录。
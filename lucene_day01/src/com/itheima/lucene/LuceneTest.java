package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {

	//创建索引库
	@Test
	public void test() throws Exception{
		//创建存储目标对象
		Directory directory = FSDirectory.open(new File("F:/test"));
		//创建一个通用分析器
		Analyzer analyzer = new StandardAnalyzer();
		//创建IndexWriterConfig对象进行配置
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer );
		//创建索引写入对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		//读取指定路径中的文件
		File path = new File("D:/第二阶段/课件/day99_lucene&solr_day01/01.参考资料/searchsource");
		//遍历,获取每一个文件
		for (File file : path.listFiles()) {
			//获取每一个文件的名称
			String FileName = file.getName();
			//获取每一个文件的路径
			String filePath = file.getPath();
			//通过commons-io提供的工具类读取文件内容
			String content = FileUtils.readFileToString(file);
			//通过commons-io提供的工具类获取文件大小
			long size = FileUtils.sizeOf(file);
			//创建Document对象
			Document document = new Document();
			//创建域对象
			Field fieldName = new TextField("name",FileName,Store.YES);
			Field fieldPath= new TextField("path",filePath,Store.YES);
			Field fieldContent = new TextField("content",content,Store.YES);
			Field fieldSize = new TextField("size",size + "",Store.YES);
			document.add(fieldName);
			document.add(fieldPath);
			document.add(fieldContent);
			document.add(fieldSize);
			indexWriter.addDocument(document);
		}
		indexWriter.commit();
		indexWriter.close();
	}
	
	//查询索引
	@Test
	public void searchIndex() throws Exception{
		//指定索引库的位置
		Directory directory = FSDirectory.open(new File("F:/test"));
		//创建读取对象
		IndexReader indexReader = DirectoryReader.open(directory);
		//创建搜索对象,并指定搜索目标
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//创建要搜索的对象,指定搜索的域,和内容
		Query query = new TermQuery(new Term("name", "apache"));
		//执行搜索,获取搜索到的内容
		TopDocs topDocs = indexSearcher.search(query, 10);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			//获取查到的id
			int doc = scoreDoc.doc;
			//通过id获取文本对象
			Document document = indexSearcher.doc(doc);
			//通过文本对象获取域的值
			System.out.println(document.get("name"));
			System.out.println(document.get("path"));
			//System.out.println(document.get("content"));
			System.out.println(document.get("size"));
		}
	}
	
	@Test
	public void TestTokenStream() throws Exception{
		//Analyzer analyzer = new StandardAnalyzer();
		//Analyzer analyzer  = new CJKAnalyzer();
		//Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream(null, "Lucene是apache传智播客软件基金会4 jakarta项目组的一个法轮功子项目，"
				+ "是一个开放源代码的全文检索引擎工具包，但它不是一个完整的全文检索引擎，而是一个全文检索引擎的架构");
		
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			System.out.println(addAttribute.toString());
		}
	}
}

package com.itheima.lucene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
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
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest2 {

	//创建索引库
	@Test
	public void testCreareIndex() throws Exception{
		Directory directory = FSDirectory.open(new File("F:/test"));
		
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config );
		File paths = new File("D:/第二阶段/课件/day99_lucene&solr_day01/01.参考资料/searchsource");
		for (File file : paths.listFiles()) {
			String fileName = file.getName();
			String filePath = file.getPath();
			String fileContent = FileUtils.readFileToString(file);
			long fileSize = FileUtils.sizeOf(file);
			
			Document document = new Document();
			Field fieldName = new TextField("name", fileName, Store.YES);
			Field fieldPath = new TextField("path", filePath, Store.YES);
			Field fieldContent = new TextField("content", fileContent, Store.YES);
			Field fieldSize = new TextField("size", fileSize + "", Store.YES);
			document.add(fieldName );
			document.add(fieldPath );
			document.add(fieldContent );
			document.add(fieldSize );
			indexWriter.addDocument(document);
		}
		indexWriter.commit();
		indexWriter.close();
	}
	
	//查询索引
	@Test
	public void searchIndex() throws Exception{
		Directory directory = FSDirectory.open(new File("F:/test"));
		
		IndexReader indexReader = DirectoryReader.open(directory);
		
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Query query = new TermQuery(new Term("name","apache"));
		TopDocs topDocs = indexSearcher.search(query , 10);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			int id = scoreDoc.doc;
			Document doc = indexSearcher.doc(id);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("path"));
			System.out.println(doc.get("size"));
		}
	}
	
	@Test
	public void TestTokenStream() throws Exception{
		//Analyzer analyzer = new StandardAnalyzer();
		//Analyzer analyzer = new CJKAnalyzer();
		//Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		//Analyzer analyzer = new PerFieldAnalyzerWrapper;
		TokenStream tokenStream = analyzer.tokenStream(null, "Lucene是apache传智播客软件基金会4 jakarta项目组的一个法轮功子项目，"
				+ "是一个开放源代码的全文检索引擎工具包，但它不是一个完整的全文检索引擎，而是一个全文检索引擎的架构");
		
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		
		while (tokenStream.incrementToken()) {
			String string = addAttribute.toString();
			System.out.println(string);
		}
	}
}

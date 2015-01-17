package com.zdawn.text.lucene.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.text.lucene.config.Config;
import com.zdawn.text.lucene.config.IndexDB;
import com.zdawn.text.lucene.config.MetaDocument;
import com.zdawn.text.lucene.config.MetaField;

public class DocDataWorker implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(DocDataWorker.class);
	
	private IndexSearcher indexSearcher = null;
	
	private int index = -1;
	
	private List<DocumentHolder> list = null;
	
	private DocDataContext context = null;
	
	private Highlighter highlighter;
	
	public DocDataWorker(IndexSearcher indexSearcher,int index,
			List<DocumentHolder> list,DocDataContext context){
		this.indexSearcher = indexSearcher;
		this.index = index;
		this.list = list;
		this.context = context;
	}
	@Override
	public void run() {
		try {
			 List<String> fieldnamesList = convertArrayToList(context.getFieldName());
			List<Map<String, Object>> dataSet = new ArrayList<Map<String,Object>>();
			for (DocumentHolder documentHolder : list) {
				if(documentHolder.getIndex()==index) continue;
				dataSet.add(getDocumentData(documentHolder,context.getConfig(),context.getDbName(),context.getQuery(),fieldnamesList));
			}
			context.setData(dataSet);
			context.finishSearch();
		} catch (Exception e) {
			log.error("DocDataWorker run", e);
		}
	}
	
	private Map<String, Object> getDocumentData(DocumentHolder documentHolder,
			Config config,String dbName,Query query, List<String> fieldnamesList) throws Exception{
		Document document = indexSearcher.doc(documentHolder.getDoc());
		String docName = document.get(MetaDocument.SysDocFieldName);
		IndexDB indexDB = config.getIndexDBMap().get(dbName);
		if(indexDB==null) throw new Exception("not found indexdb name="+dbName);
		//get document
		MetaDocument metaDoc = indexDB.getDocument(docName);
		if(metaDoc==null) throw new Exception("not found document name="+docName);
		Map<String, Object> one = new HashMap<String, Object>();
		one.put("__score", documentHolder.getScore());
		List<MetaField> metaFieldList = metaDoc.getFieldList();
		if(fieldnamesList!=null){
			List<MetaField> filterMetaFieldList = new ArrayList<MetaField>();
			for (MetaField metaField : metaFieldList) {
				if(fieldnamesList.contains(metaField.getFieldName())) filterMetaFieldList.add(metaField);
			}
			if(filterMetaFieldList.size()>0) metaFieldList = filterMetaFieldList;
		}
		for (MetaField metaField : metaFieldList) {
			if(!metaField.getFieldType().stored()) continue;
			IndexableField indexableField = document.getField(metaField.getFieldName());
			if(indexableField==null) continue;
			if(metaField.getDataType().equals(MetaField.INT_FIELD)){
				one.put(metaField.getFieldName(),indexableField.numericValue());
			}else if(metaField.getDataType().equals(MetaField.LONG_FIELD)){
				one.put(metaField.getFieldName(),indexableField.numericValue());
			}else if(metaField.getDataType().equals(MetaField.FLOAT_FIELD)){
				one.put(metaField.getFieldName(),indexableField.numericValue());
			}else if(metaField.getDataType().equals(MetaField.DOUBLE_FIELD)){
				one.put(metaField.getFieldName(),indexableField.numericValue());
			}else if(metaField.getDataType().equals(MetaField.STRING_FIELD) && !metaField.isDocValues()){
				one.put(metaField.getFieldName(),indexableField.stringValue());
			}else if(metaField.getDataType().equals(MetaField.FILE_FIELD)){
				//返回摘要
				if(highlighter==null) initHighlighter(query, config);
				String result = "";
				try {
					Terms termVector = indexSearcher.getIndexReader().getTermVector(documentHolder.getDoc(),metaField.getFieldName()); 
					TokenStream tokenStream = TokenSources.getTokenStream(termVector);    
					result = highlighter.getBestFragment(tokenStream,indexableField.stringValue());
				} catch (Exception e) {
					System.out.println(e.toString());
				} 
				one.put(metaField.getFieldName(),result);
			}
		}
		return one;
	}
	
	private void initHighlighter(Query query,Config config){
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>","</font>");
		highlighter =new Highlighter(simpleHTMLFormatter,new QueryScorer(query));
		int fragmentSize = 200;
		String temp = config.getConfigPara("highlighter.fragmentSize");
		if(temp !=null) fragmentSize = Integer.parseInt(temp);
		highlighter.setTextFragmenter(new SimpleFragmenter(fragmentSize));
	}
	private List<String> convertArrayToList(String[] array){
		if(array==null) return null;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) list.add(array[i]);
		return list;
	}
}

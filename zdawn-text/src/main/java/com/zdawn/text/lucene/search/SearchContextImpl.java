package com.zdawn.text.lucene.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;

import com.zdawn.text.lucene.config.Config;
import com.zdawn.text.lucene.config.IndexDB;
import com.zdawn.text.lucene.config.MetaDocument;
import com.zdawn.text.lucene.config.MetaField;

public class SearchContextImpl implements SearchContext {
	//store search data
	private List<IndexSearcher> indexSearcherList= null;
	private TopDocs[]  topDocArray = null; 
	private CountDownLatch countLatch = null;
	private int totalHits;
	private Highlighter highlighter;
	/**
	 * @param count 并行检索线程数
	 */
	public SearchContextImpl(List<IndexSearcher> indexSearcherList){
		this.indexSearcherList = indexSearcherList;
		countLatch = new CountDownLatch(indexSearcherList.size());
		topDocArray = new TopDocs[indexSearcherList.size()];
	}
	/**
	 * 标记完成一个线程检索
	 */
	public void finishSearch(){
		countLatch.countDown();
	}
	/**
	 * 当前线程等待并行检索线程全部完成
	 * @param millisTimeout 等待时间，如果超过这个时间会返回，如果线程全部完成可以提前返回。
	 * @throws InterruptedException
	 */
	public void  await(long millisTimeout)  throws InterruptedException{
		countLatch.await(millisTimeout,TimeUnit.MILLISECONDS);
	}
	/**
	 * 设置单个查询线程查询数据
	 * @param key 检索数据标识
	 * @param data 数据
	 */
	public void setThreadData(Object key,Object data){
		if(key instanceof IndexSearcher && data!=null){
			if(data instanceof TopDocs){
				for (int i = 0; i < indexSearcherList.size(); i++) {
					IndexSearcher indexSearcher = indexSearcherList.get(i);
					if(indexSearcher==key){
						topDocArray[i] = (TopDocs)data;
					}
				}
			}else{
				System.out.println("data not TopDocs class "+data.getClass().getName());
			}
		}else{
			System.out.println("index to large index="+key+" or data is null");
		}
	}
	
	public int getTotalHits() {
		return totalHits;
	}
	//获取查询数据
	public List<Map<String, Object>> getQueryData(Config config,String dbName,
			Query query, int topNum,String[] fieldName) throws Exception{
		List<Map<String, Object>> dataSet = new ArrayList<Map<String,Object>>();
		List<DocumentHolder> list = mergeTopDocs(topNum);
		List<String> fieldnamesList = convertArrayToList(fieldName);
		for (DocumentHolder documentHolder : list) {
			dataSet.add(getDocumentData(documentHolder,config,dbName,query,fieldnamesList));
		}
		return dataSet;
	}
	//分页获取查询数据
	public List<Map<String, Object>> getPageQueryData(Config config,String dbName,
			Query query, int currentPage,int pageCount,int topNum,String[] fieldName) throws Exception{
		List<Map<String, Object>> dataSet = new ArrayList<Map<String,Object>>();
		List<DocumentHolder> list = mergePageTopDocs(currentPage,pageCount,topNum);
		List<String> fieldnamesList = convertArrayToList(fieldName);
		for (DocumentHolder documentHolder : list) {
			dataSet.add(getDocumentData(documentHolder,config,dbName,query,fieldnamesList));
		}
		return dataSet;
	}
	
	private Map<String, Object> getDocumentData(DocumentHolder documentHolder,
			Config config,String dbName,Query query, List<String> fieldnamesList) throws Exception{
		IndexSearcher indexSearcher = indexSearcherList.get(documentHolder.getIndex());
		Document document = indexSearcher.doc(documentHolder.getDoc());
		String docName = document.get(MetaDocument.SysDocFieldName);
		IndexDB indexDB = config.getIndexDBMap().get(dbName);
		if(indexDB==null) throw new Exception("not found indexdb name="+dbName);
		//get document
		MetaDocument metaDoc = indexDB.getDocument(docName);
		if(metaDoc==null) throw new Exception("not found document name="+docName);
		Map<String, Object> one = new HashMap<String, Object>();
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
	
	private List<DocumentHolder> mergePageTopDocs(int currentPage,
			int pageSize, int topNum) {
		int start = (currentPage-1)*pageSize;
		int end = currentPage*pageSize > topNum ? topNum-1:currentPage*pageSize-1;
		for (int i = 0; i < topDocArray.length; i++){
			if(topDocArray[i]==null) continue;
			ScoreDoc[] temp = topDocArray[i].scoreDocs;
			if(temp ==null || temp.length==0) continue;
			if(start>temp.length-1) {
				topDocArray[i].scoreDocs = null;
				continue;
			}
			int endLength = end >temp.length-1 ? temp.length-1: end;
			int length = endLength-start +1;
			ScoreDoc[] subTemp = new ScoreDoc[length];
			System.arraycopy(temp, start, subTemp, 0, length);
			topDocArray[i].scoreDocs = subTemp;
		}
		return mergeTopDocs(pageSize);
	}
	
	private List<DocumentHolder> mergeTopDocs(int maxNum){
		LinkedList<DocumentHolder> list = new LinkedList<DocumentHolder>();
		boolean initList = false;
		for (int i = 0; i < topDocArray.length; i++) {
			if(topDocArray[i]==null) continue;
			totalHits = totalHits +topDocArray[i].totalHits;
			ScoreDoc[] temp = topDocArray[i].scoreDocs;
			if(temp ==null || temp.length==0) continue;
			if(temp[0].score==Float.NEGATIVE_INFINITY) continue;
			if(!initList){//任意一个TopDocs初始化
				for (ScoreDoc scoreDoc : temp) {
					if(scoreDoc==null) continue;
					if(scoreDoc.score==Float.NEGATIVE_INFINITY) break;
					list.add(new DocumentHolder(i, scoreDoc.score, scoreDoc.doc));
				}
				initList = true;
			}else{//合并
				for (ScoreDoc scoreDoc : temp) {
					if(scoreDoc==null) continue; 
					if(scoreDoc.score==Float.NEGATIVE_INFINITY) break;
					if(maxNum==list.size()){
						//取最后一个元素,与当前比较,大于当前元素退出循环
						DocumentHolder holder = list.peekLast();
						if(holder.getScore()>=scoreDoc.score) break;
					}
					insertDocumentHolder(list,scoreDoc,i,maxNum);
				}
			}
		}
		return list;
	}
	//插入元素
	private void insertDocumentHolder(LinkedList<DocumentHolder> list,
			ScoreDoc scoreDoc,int index, int topNum) {
		if(topNum>list.size()){
			list.add(new DocumentHolder(index, scoreDoc.score, scoreDoc.doc));
			return;
		}
		for (int j = 0; j < list.size(); j++) {
			DocumentHolder holder = list.get(j);
			if(scoreDoc.score>holder.getScore()){
				list.add(j, new DocumentHolder(index, scoreDoc.score, scoreDoc.doc));
				if(list.size()>topNum) list.pollLast();
				return;
			}
		}
	}
	private void initHighlighter(Query query,Config config){
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color=’red’>","</font>");
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

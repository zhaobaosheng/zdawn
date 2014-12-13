package com.zdawn.text.lucene.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import com.zdawn.commons.concurrent.task.DisposeUnit;
import com.zdawn.text.lucene.config.Config;
import com.zdawn.text.lucene.factory.IndexSearcherFactory;

public class SeacherImple implements Seacher {
	private IndexSearcherFactory indexSearcherFactory;
	//dispose unit
	private List<DisposeUnit> disposeUnitList;
	
	private Config config;
	//search wait time unit millisecond
	private int millisTimeout = 3000;
	@Override
	public List<Map<String, Object>> searchTopDocument(String dbName,
			Map<String, String> para) throws Exception{
		//validate input parameter
		String queryString = para.get("queryString");
		if(queryString==null || queryString.equals("")) throw new Exception("queryString must input");
		String defaultField = para.get("defaultField");
		if(defaultField==null || defaultField.equals("")) throw new Exception("defaultField must input");
		//get searcher
		List<IndexSearcher> indexSearcherList=indexSearcherFactory.getIndexSearchers(dbName);
		SearchContextImpl context = new SearchContextImpl(indexSearcherList);
		
		QueryParser qp = new QueryParser(Version.LUCENE_4_9,defaultField, config.getAnalyzer());
		qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = qp.parse(queryString);
		//judge access
		String accessValues = para.get("accessValues");
		if(accessValues!=null){
			String accessFieldName = para.get("accessFieldName");
			if(accessFieldName==null || accessFieldName.equals("")) throw new Exception("accessFieldName must input");
			query = new AccessScoreQuery(query, accessFieldName, accessValues) ;
		}
		String stringNum = para.get("topNum");
		int topNum = stringNum==null || stringNum.equals("") ? 100:Integer.parseInt(stringNum);
		
		for (int i = 0; i < indexSearcherList.size(); i++) {
			IndexSearcher indexSearcher = indexSearcherList.get(i);
			SearchWorker worker = new SearchWorker(indexSearcher, query, context);
			worker.setCount(topNum);
			putTaskByQueueDepth(worker);
		}
		context.await(millisTimeout);
		//collect search result
		List<Map<String, Object>> dataSet = context.getQueryData(config, dbName, query);
		//sort other field fieldName desc or asc
		String sort = para.get("sort");
		sort = sort==null ? "":sort;
		if(sort.length()>0){
			String sortDataType = para.get("sortDataType");
			sortDataType = sortDataType==null ? "string":sortDataType;
			String sortAsc = para.get("sortAsc");
			sortAsc = sortAsc==null ? "false":sortAsc;
			Comparator<Map<String, Object>> comparator = null;
			if(sortDataType.equals("number")){
				comparator = new NumberFieldComparator(sort,sortAsc);
			}else{
				comparator = new StringFieldComparator(sort,sortAsc);
			}
			Collections.sort(dataSet, comparator);
		}
		return dataSet;
	}

	@Override
	public Map<String, Object> searchPageDocument(String dbName,
			Map<String, String> para) throws Exception{
		//validate input parameter
		String queryString = para.get("queryString");
		if(queryString==null || queryString.equals("")) throw new Exception("queryString must input");
		String defaultField = para.get("defaultField");
		if(defaultField==null || defaultField.equals("")) throw new Exception("defaultField must input");
		//get searcher
		List<IndexSearcher> indexSearcherList=indexSearcherFactory.getIndexSearchers(dbName);
		SearchContextImpl context = new SearchContextImpl(indexSearcherList);
		
		QueryParser qp = new QueryParser(Version.LUCENE_4_9,defaultField, config.getAnalyzer());
		qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = qp.parse(queryString);
		//judge access
		String accessValues = para.get("accessValues");
		if(accessValues!=null){
			String accessFieldName = para.get("accessFieldName");
			if(accessFieldName==null || accessFieldName.equals("")) throw new Exception("accessFieldName must input");
			query = new AccessScoreQuery(query, accessFieldName, accessValues) ;
		}
		String stringNum = para.get("topNum");
		int topNum = stringNum==null || stringNum.equals("") ? 100:Integer.parseInt(stringNum);
		
		for (int i = 0; i < indexSearcherList.size(); i++) {
			IndexSearcher indexSearcher = indexSearcherList.get(i);
			SearchWorker worker = new SearchWorker(indexSearcher, query, context);
			worker.setCount(topNum);
			putTaskByQueueDepth(worker);
		}
		context.await(millisTimeout);
		//collect search result
		List<Map<String, Object>> dataSet = context.getQueryData(config, dbName, query);
		Map<String, Object> map = new HashMap<String, Object>();
		if(dataSet.size()>0){
			//sort other field fieldName desc or asc
			String sort = para.get("sort");
			sort = sort==null ? "":sort;
			if(sort.length()>0){
				String sortDataType = para.get("sortDataType");
				sortDataType = sortDataType==null ? "string":sortDataType;
				String sortAsc = para.get("sortAsc");
				sortAsc = sortAsc==null ? "false":sortAsc;
				Comparator<Map<String, Object>> comparator = null;
				if(sortDataType.equals("number")){
					comparator = new NumberFieldComparator(sort,sortAsc);
				}else{
					comparator = new StringFieldComparator(sort,sortAsc);
				}
				Collections.sort(dataSet, comparator);
			}
			//分页返回数据
			int page = 1,pageSize = 10,pageCount=0;
			String temp = para.get("page");
			if(temp!=null) page = Integer.parseInt(temp);
			temp = para.get("rows");
			if(temp!=null) pageSize = Integer.parseInt(temp);
			if(dataSet.size()<pageSize) pageSize = dataSet.size();
			if (dataSet.size() % pageSize == 0) pageCount = dataSet.size() / pageSize;
			else pageCount = dataSet.size() / pageSize + 1;
			page = page > pageCount ? pageCount:page;
			int start = (page-1)*pageSize;
			int end = page*pageSize > dataSet.size() ? dataSet.size()-1:page*pageSize-1;
			map.put("rows",dataSet.subList(start, end));
		}else{
			map.put("rows",dataSet);
		}
		map.put("total",dataSet.size());
		map.put("cacheId",-1);
		map.put("totalHits",context.getTotalHits());
		return map;
	}
	/**
	 * put search task by disposeUnit available queue depth.
	 */
	private void putTaskByQueueDepth(Runnable task) throws Exception{
		DisposeUnit currentDisposeUnit = null;
		int availableSize = 0;
		for (DisposeUnit disposeUnit : disposeUnitList) {
			int size = disposeUnit.availableTaskQueueSize();
			if(size>availableSize){
				currentDisposeUnit = disposeUnit;
				availableSize = size;
			}
		}
		if(availableSize==0) throw new Exception("DisposeUnit can not execute search task");
		currentDisposeUnit.handleTask(task);
	}

	public void setIndexSearcherFactory(IndexSearcherFactory indexSearcherFactory) {
		this.indexSearcherFactory = indexSearcherFactory;
	}

	public void setDisposeUnitList(List<DisposeUnit> disposeUnitList) {
		this.disposeUnitList = disposeUnitList;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setMillisTimeout(int millisTimeout) {
		this.millisTimeout = millisTimeout;
	}
}

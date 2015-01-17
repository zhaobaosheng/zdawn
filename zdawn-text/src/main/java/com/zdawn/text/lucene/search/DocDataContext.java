package com.zdawn.text.lucene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.search.Query;

import com.zdawn.text.lucene.config.Config;

public class DocDataContext {
	
	private CountDownLatch countLatch = null;
	
	private List<Map<String, Object>> dataSet = null;
	
	private Config config;
	
	private String dbName;
	
	private Query query;
	
	private String[] fieldName;
	
	public DocDataContext(int count){
		countLatch = new CountDownLatch(count);
	}
	public void finishSearch(){
		countLatch.countDown();
	}
	public void  await(long millisTimeout)  throws InterruptedException{
		countLatch.await(millisTimeout,TimeUnit.MILLISECONDS);
	}
	public synchronized void setData(List<Map<String, Object>> temp){
		if(dataSet==null) dataSet = new ArrayList<Map<String, Object>>();
		dataSet.addAll(temp);
	}
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public String[] getFieldName() {
		return fieldName;
	}
	public void setFieldName(String[] fieldName) {
		this.fieldName = fieldName;
	}
	public List<Map<String, Object>> getDataSet() {
		return dataSet==null ? new ArrayList<Map<String, Object>>():dataSet;
	}
}

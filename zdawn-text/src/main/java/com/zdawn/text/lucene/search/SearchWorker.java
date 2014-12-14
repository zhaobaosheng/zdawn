package com.zdawn.text.lucene.search;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 执行每个索引文件的检索
 * @author zhaobs
 *  2014-11-08
 */
public class SearchWorker implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(SearchWorker.class);
	
	private IndexSearcher indexSearcher = null;
	
	private Query query = null;
	//检索最大记录数
	private int topNum  = 100;

	private SearchContext context = null;
	
	public SearchWorker(IndexSearcher indexSearcher,Query query,SearchContext context){
		this.indexSearcher = indexSearcher;
		this.query = query;
		this.context = context;
	}
	
	public int getCount() {
		return topNum ;
	}

	public void setCount(int topNum ) {
		this.topNum  = topNum ;
	}

	@Override
	public void run() {
		try {
			TopDocs topDocs = indexSearcher.search(query , topNum);
			context.setThreadData(indexSearcher, topDocs);
			context.finishSearch();
		} catch (Exception e) {
			log.error("SearchWorker run", e);
		}
	}
}

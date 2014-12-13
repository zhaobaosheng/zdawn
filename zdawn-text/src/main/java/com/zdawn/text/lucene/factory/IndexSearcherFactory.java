package com.zdawn.text.lucene.factory;

import java.util.List;

import org.apache.lucene.search.IndexSearcher;

public interface IndexSearcherFactory {
	/**
	 * gain all IndexSearchers for index db
	 * @param dbName 索引库名字
	 */
	public List<IndexSearcher> getIndexSearchers(String dbName);
}

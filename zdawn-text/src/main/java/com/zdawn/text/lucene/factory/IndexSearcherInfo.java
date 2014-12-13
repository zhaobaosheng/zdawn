package com.zdawn.text.lucene.factory;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public class IndexSearcherInfo {
	private IndexSearcher indexSearcher;
	
	private String path;
	
	private Directory directory;

	public IndexSearcher getIndexSearcher() {
		return indexSearcher;
	}

	public void setIndexSearcher(IndexSearcher indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
}

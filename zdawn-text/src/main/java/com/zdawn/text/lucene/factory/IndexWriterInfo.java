package com.zdawn.text.lucene.factory;

import org.apache.lucene.index.IndexWriter;

public class IndexWriterInfo {
	//indexWriter
	private IndexWriter indexWriter;
	//index file path
	private String path;
	//index file size
	private long indexFileSize = 0L;
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}
	public void setIndexWriter(IndexWriter indexWriter) {
		this.indexWriter = indexWriter;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getIndexFileSize() {
		return indexFileSize;
	}
	public void setIndexFileSize(long indexFileSize) {
		this.indexFileSize = indexFileSize;
	}
}

package com.zdawn.text.lucene.factory;

import java.util.List;

import org.apache.lucene.index.IndexWriter;

public interface IndexWriterFactory {
	/**
	 * current working IndexWriter
	 * <br> it can create index
	 */
	public IndexWriter getWorkingIndexWriter(String dbName);
	/**
	 * gain a group retired IndexWriters
	 * retired IndexWriter can not create index
	 */
	public List<IndexWriter> getRetiredIndexWriter(String dbName);
	/**
	 * gain all index db working IndexWriter
	 */
	public List<IndexWriter> getWorkingIndexWriters(String dbName);
	
}

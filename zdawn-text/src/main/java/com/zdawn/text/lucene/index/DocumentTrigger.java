package com.zdawn.text.lucene.index;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

public interface DocumentTrigger {
	/**
	 * only support one document successfully created  it will trigger
	 */
	public void createDocument(String dbName,IndexWriter indexWriter,
			Iterable<? extends IndexableField> doc);
	/**
	 * only support one document successfully update  it will trigger
	 */
	public void updateDocument(String dbName,IndexWriter indexWriter,
			Term term, Iterable<? extends IndexableField> doc);
	/**
	 * every delete document will trigger
	 * <br>only support identify term  delete one document
	 */
	public void deleteDocument(String dbName,IndexWriter indexWriter,Query... queries);
}

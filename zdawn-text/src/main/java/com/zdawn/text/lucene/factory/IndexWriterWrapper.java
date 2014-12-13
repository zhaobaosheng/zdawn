package com.zdawn.text.lucene.factory;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

import com.zdawn.text.lucene.index.DocumentTrigger;

public class IndexWriterWrapper extends IndexWriter {
	
	private String dbName = null;
	
	private DocumentTrigger trigger = null;
	
	private boolean dirty;
	
	public IndexWriterWrapper(Directory directory, IndexWriterConfig conf)
			throws IOException {
		super(directory, conf);
	}
	@Override
	public void updateDocument(Term term,
			Iterable<? extends IndexableField> doc, Analyzer analyzer)
			throws IOException {
		super.updateDocument(term, doc, analyzer);
		if(!dirty) dirty = true;
		if(trigger != null){
			if(term==null)  trigger.createDocument(dbName,this,doc);
			else  trigger.updateDocument(dbName,this,term,doc);
		}
	}
	
	@Override
	public void deleteDocuments(Query... queries) throws IOException {
		super.deleteDocuments(queries);
		if(!dirty) dirty = true;
		if(trigger != null) trigger.deleteDocument(dbName, this, queries);
	}

	public void setTrigger(DocumentTrigger trigger) {
		this.trigger = trigger;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public boolean isDirty() {
		return dirty;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
}

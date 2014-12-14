package com.zdawn.text.lucene.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.CustomScoreProvider;
/**
 * 检查权限
 * @author zhaobs
 */
public class AccessQueryScoreProvider extends CustomScoreProvider {
	//存储权限名字
	private String accessFieldName = null;
	
	private String[] accessValue = null;
	
	public AccessQueryScoreProvider(AtomicReaderContext context){
		super(context);
	}
	@Override
	public float customScore(int doc, float subQueryScore, float[] valSrcScores)
			throws IOException {
		IndexReader indexReader = context.reader();
		Document document = indexReader.document(doc);
		String value = document.get(accessFieldName);
		if(!haveAccess(value)) return 0f;
		return  super.customScore(doc, subQueryScore, valSrcScores);
	}
	public void setAccessFieldName(String accessFieldName) {
		this.accessFieldName = accessFieldName;
	}
	public void setAccessValue(String[] accessValue) {
		this.accessValue = accessValue;
	}
	private boolean haveAccess(String value){
		if(accessValue==null) return false;
		for (String tmpAccess : accessValue) {
			if(tmpAccess.startsWith(value)) return true;
		}
		return false;
	}
}

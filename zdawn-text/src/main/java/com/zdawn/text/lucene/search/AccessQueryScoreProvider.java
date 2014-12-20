package com.zdawn.text.lucene.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.CustomScoreProvider;
/**
 * 检查权限
 * @author zhaobs
 */
public class AccessQueryScoreProvider extends CustomScoreProvider {
	//存储权限名字
	private String accessFieldName = null;
	
	private String[] accessValue = null;
	
	private boolean useDocValues = true;
	
	private SortedDocValues docValues = null;
	
	public AccessQueryScoreProvider(AtomicReaderContext context){
		super(context);
	}
	public void initDocValue() {
		try {
			AtomicReader indexReader = context.reader();
			docValues = indexReader.getSortedDocValues(accessFieldName);
			if( docValues ==null) useDocValues = false;
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	@Override
	public float customScore(int doc, float subQueryScore, float[] valSrcScores)
			throws IOException {
		String value = null;
		if(useDocValues){
			value = docValues.get(doc).utf8ToString();			
		}else{
			Document document = context.reader().document(doc);
			value = document.get(accessFieldName);
		}
		if(!haveAccess(value)) return Float.NEGATIVE_INFINITY;
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

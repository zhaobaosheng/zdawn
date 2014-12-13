package com.zdawn.text.lucene.search;


/**
 * @author zhaobs
 * 该类合并检索结果使用
 */
public class DocumentHolder {
	//IndexSearcher index
	private int index = 0;
	//query score
	private float score;
	//document's number.
	private int doc;
	
	public DocumentHolder(int index,float score, int doc){
		this.index = index;
		this.score = score;
		this.doc = doc;
	}

	public int getIndex() {
		return index;
	}

	public float getScore() {
		return score;
	}

	public int getDoc() {
		return doc;
	}
}

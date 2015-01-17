package com.zdawn.text.lucene.search;

import java.util.Comparator;


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
class DocIdComparator implements Comparator<DocumentHolder> {

	@Override
	public int compare(DocumentHolder o1, DocumentHolder o2) {
		if (o1 == null || o2 == null) return 0;
        if (o1 == o2) return 0;
        if (o1.getDoc() == o2.getDoc()) return 0;
		return o1.getDoc()>o2.getDoc() ?1:-1 ;
	}
}
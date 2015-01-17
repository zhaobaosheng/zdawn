package com.zdawn.text.lucene.search;

import java.text.Collator;
import java.util.Comparator;
import java.util.Map;
/**
 * 按字符串字段排序
 * @author zhaobs
 */
public class StringFieldComparator implements Comparator<Map<String, Object>> {
	private String fieldName;
	private boolean asc;
	private Collator collator = Collator.getInstance();
	
	public StringFieldComparator(String fieldName,String asc){
		this.fieldName = fieldName;
		this.asc =  Boolean.valueOf(asc);
	}
	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		if (o1 == null || o2 == null) return 0;
        if (o1 == o2) return 0;
        Object value1 = o1.get(fieldName);
        if(value1==null) value1 = "";
        Object value2 = o2.get(fieldName);
        if(value2==null) value2 = "";
        int result = collator.compare(value1.toString(), value2.toString());
		return asc ? result:-result;
	}
	
}

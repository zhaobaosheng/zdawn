package com.zdawn.text.lucene.search;

import java.util.Comparator;
import java.util.Map;
/**
 * 按数字类型字段排序
 * @author zhaobs
 */
public class NumberFieldComparator implements Comparator<Map<String, Object>> {
	private String fieldName;
	private boolean asc;
	
	public NumberFieldComparator(String fieldName,String asc){
		this.fieldName = fieldName;
		this.asc = Boolean.valueOf(asc);
	}
	@Override
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		if (o1 == null || o2 == null) return 0;
        if (o1 == o2) return 0;
        Object value1 = o1.get(fieldName);
        Number n1 = 0;
        if(value1 == null){
        }else if(value1 instanceof Number){
        	if(value1 != null) n1 = (Number)value1;
        }else{
        	n1 = Double.valueOf(value1.toString());
        }
        Object value2 = o2.get(fieldName);
        Number n2 = 0;
        if(value2 == null){
        }else if(value2 instanceof Number){
        	if(value2 != null) n2 = (Number)value2;
        }else{
        	n2 = Double.valueOf(value2.toString());
        }
        int result =n1.doubleValue()==n2.doubleValue()?0:n1.doubleValue()> n2.doubleValue() ? 1:-1;
		return asc ? result:-result;
	}
}

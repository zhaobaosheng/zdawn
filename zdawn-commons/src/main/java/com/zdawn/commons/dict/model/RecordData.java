package com.zdawn.commons.dict.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 编码表对应一条记录
 * @author zhaobs
 * string:java.lang.String
 * number:java.long.Long
 * date:java.sql.Timestamp
 */
public class RecordData {

	private Map<String, Object> values = new HashMap<String, Object>(8);

    public void putKeyValue(String propertyName,Object obj){
        values.put(propertyName,obj);
    }

    public Object getValue(String propertyName){
        return values.get(propertyName);
    }

	public Collection<Object> getAllRecordData(){
	    return values.values();
	}
}

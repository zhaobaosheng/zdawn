package com.zdawn.tools.modeleditor;

import java.io.Serializable;

public class KeyValue implements Serializable{
	private static final long serialVersionUID = 2583389810966333781L;

	private String key = "";
	
	private String value  = "";
	
	public KeyValue(){
	}
	public KeyValue(String key,String value){
		this.key = key;
		this.value = value;
	}
	
	public boolean equalsKey(String oneKey){
		if(key==null) return false;
		return key.equals(oneKey);
	}
	@Override
	public String toString() {
		return value;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj instanceof KeyValue){
			KeyValue temp = (KeyValue)obj;
			return equalsKey(temp.getKey());
		}
		return false;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

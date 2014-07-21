package com.zdawn.commons.dict.model;

import java.io.Serializable;
/***
 * 字段实体类
 * @author zhaobs
 */
public class DataField implements Serializable {
	private static final long serialVersionUID = 9115967785452062810L;
	/**字段名称*/
	private String fieldName = "";
	/**数据类型 string number date*/
	private String dataType = "";
	/**是否显示字段*/
	private boolean isDisplay = false;
	/**字段描述*/
	private String description = "";
	/**日期to字符串格式*/
	private String toStringformat = "yyyy-MM-dd";
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public boolean isDisplay() {
		return isDisplay;
	}
	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getToStringformat() {
		return toStringformat;
	}
	public void setToStringformat(String toStringformat) {
		this.toStringformat = toStringformat;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj instanceof DataField){
			return ((DataField)obj).getFieldName().equals(getFieldName());
		}
		return false;
	}
	
}

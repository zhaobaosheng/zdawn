package com.zdawn.commons.sqlquery.model;
/**
 * 查询参数描述项
 * @author zhaobs
 */
public class ParameterItem {
	//数据库字段名
	private String column;
	//输入参数表识
	private String property;
	//java数据类型
	private String type;
	//日期转换字符转格式
	private String toStringformat;
	//可为空
	private boolean empty = true;
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getToStringformat() {
		return toStringformat;
	}
	public void setToStringformat(String toStringformat) {
		this.toStringformat = toStringformat;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
}

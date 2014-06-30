package com.zdawn.commons.sqlquery.model;

/**
 * 查询结果项描述
 * @author zhaobs
 *
 */
public class ResultItem {
	//数据库字段名
	private String column;
	//输入参数表识
	private String property;
	//java数据类型
	private String type;
	//日期转换字符转格式
	private String toStringformat;
	
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
}

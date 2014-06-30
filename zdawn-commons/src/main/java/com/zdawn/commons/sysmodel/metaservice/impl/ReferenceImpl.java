package com.zdawn.commons.sysmodel.metaservice.impl;

import java.io.Serializable;

import com.zdawn.commons.sysmodel.metaservice.Reference;

public class ReferenceImpl implements Reference,Serializable {
	private static final long serialVersionUID = -3710203405092022641L;
	/**
	 * 引用实体名称
	 */
	private String entityName;
	/**
	 * 引用表名
	 */
	private String tableName;
	/**
	 * 引用属性名
	 */
	private String propertyName;
	/**
	 * 引用字段名
	 */
	private String column;
	/**
	 * 引用类型codeTable or table
	 */
	private String type;
	/**
	 * 显示属性
	 */
	private String displayPropertyName;
	/**
	 * 显示字段
	 */
	private String displayColumn;
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplayPropertyName() {
		return displayPropertyName;
	}
	public void setDisplayPropertyName(String displayPropertyName) {
		this.displayPropertyName = displayPropertyName;
	}
	public String getDisplayColumn() {
		return displayColumn;
	}
	public void setDisplayColumn(String displayColumn) {
		this.displayColumn = displayColumn;
	}
}

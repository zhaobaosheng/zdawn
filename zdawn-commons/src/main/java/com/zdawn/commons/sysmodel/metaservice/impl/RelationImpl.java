package com.zdawn.commons.sysmodel.metaservice.impl;

import java.io.Serializable;

import com.zdawn.commons.sysmodel.metaservice.Relation;
import com.zdawn.util.Utils;

public class RelationImpl implements Relation,Serializable {
	private static final long serialVersionUID = -3420333768331780051L;
	/**
	 *关联属性名
	 */
	private String selfPropertyName;
	/**
	 * 关联字段名
	 */
	private String selfColumn;
	/**
	 * 实体关系描述
	 */
	private String description;
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
	 * 关系类型oneToMany or oneToOne
	 */
	private String type;
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj instanceof Relation){
			Relation one = (Relation)obj;
			boolean result = false;
			if(!Utils.isEmpty(selfPropertyName) && !Utils.isEmpty(one.getSelfPropertyName())){
				if(selfPropertyName.equalsIgnoreCase(one.getSelfPropertyName())) result = true;
			}
			if(!Utils.isEmpty(selfColumn) && !Utils.isEmpty(one.getSelfColumn())){
				if(selfColumn.equalsIgnoreCase(one.getSelfColumn())) result = true;
			}
			if(!result) return false;
			
			result = false;
			if(!Utils.isEmpty(entityName) && !Utils.isEmpty(one.getEntityName())){
				if(entityName.equalsIgnoreCase(one.getEntityName())) result = true;
			}
			if(!Utils.isEmpty(tableName) && !Utils.isEmpty(one.getTableName())){
				if(tableName.equalsIgnoreCase(one.getTableName())) result = true;
			}
			if(!result) return false;
			
			result = false;
			if(!Utils.isEmpty(propertyName) && !Utils.isEmpty(one.getPropertyName())){
				if(propertyName.equalsIgnoreCase(one.getPropertyName())) result = true;
			}
			if(!Utils.isEmpty(column) && !Utils.isEmpty(one.getColumn())){
				if(column.equalsIgnoreCase(one.getColumn())) result = true;
			}
			return result;
		}
		return false;
	}
	
	public String getSelfPropertyName() {
		return selfPropertyName;
	}
	public void setSelfPropertyName(String selfPropertyName) {
		this.selfPropertyName = selfPropertyName;
	}
	public String getSelfColumn() {
		return selfColumn;
	}
	public void setSelfColumn(String selfColumn) {
		this.selfColumn = selfColumn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
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
	
}

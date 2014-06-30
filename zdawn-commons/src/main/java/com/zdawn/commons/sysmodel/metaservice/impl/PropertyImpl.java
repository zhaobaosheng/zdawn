package com.zdawn.commons.sysmodel.metaservice.impl;

import java.io.Serializable;

import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.Reference;

public class PropertyImpl implements Property,Serializable {
	private static final long serialVersionUID = 5642141325233052859L;
	/**
	 * true-启用 false-停用
	 */
	private boolean using = false;
	/**
	 * 属性名
	 */
	private String name;
	/**
	 * 字段名
	 */
	private String column;
	/**
	 * 属性中文描述
	 */
	private String description;
	/**
	 * 字段数据类型
	 */
	private String type;
	/**
	 * 长度
	 */
	private int length = 0;
	/**
	 * 精度
	 */
	private int scale = 0;
	/**
	 * false可为空
	 * true不能为空
	 */
	private boolean notNull= false;
	/**
	 * 缺省值
	 */
	private String defaultValue;
	/**
	 * 转换String格式
	 */
	private String toStringformat;
	/**
	 * 引用编码或者表
	 */
	private Reference reference;
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj instanceof Property){
			Property one = (Property)obj;
			return name.equalsIgnoreCase(one.getName());
		}
		return false;
	}
	
	public boolean isUsing() {
		return using;
	}
	public void setUsing(boolean using) {
		this.using = using;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getToStringformat() {
		return toStringformat;
	}
	public void setToStringformat(String toStringformat) {
		this.toStringformat = toStringformat;
	}
	public Reference getReference() {
		return reference;
	}
	public void setReference(Reference reference) {
		this.reference = reference;
	}
}

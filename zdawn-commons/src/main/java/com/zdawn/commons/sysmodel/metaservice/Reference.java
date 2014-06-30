package com.zdawn.commons.sysmodel.metaservice;

public interface Reference {
	/**
	 * 返回引用实体名称
	 */
	public String getEntityName();
	/**
	 * 返回引用表名
	 */
	public String getTableName();
	/**
	 * 返回引用属性名
	 */
	public String getPropertyName();
	/**
	 * 返回引用字段名
	 */
	public String getColumn();
	/**
	 * 返回引用类型codeTable or table
	 */
	public String getType();
	/**
	 * 返回显示属性名
	 */
	public String getDisplayPropertyName();
	/**
	 * 返回显示字段名
	 */
	public String getDisplayColumn();
}

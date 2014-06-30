package com.zdawn.commons.sysmodel.metaservice;

import java.util.List;

public interface Entity {
	/**
	 * 实体名称 
	 */
	public String getName();
	/**
	 * 表名 
	 */
	public String getTableName();
	/**
	 * 实体描述 
	 */
	public String getDescription();
	/**
	 * 实体类型 
	 */
	public String getType();
	/**
	 * 主键字段
	 */
	public String getUniqueColumn();
	/**
	 * 对应java类 可以为空
	 */
	public String getClazz();
	/**
	 * 实体关系
	 */
	public List<Relation> getRelations();
	/**
	 * 实体属性信息 
	 */
	public List<Property> getProperties();
	/**
	 * 查找主键属性
	 */
	public Property findUniqueColumnProperty();
	/**
	 * 通过属性名查找属性
	 * @param propertyName 属性名
	 * @return Property
	 */
	public Property findPropertyByName(String propertyName);
	/**
	 * 通过字段名查找属性
	 * @param column 字段名
	 * @return Property
	 */
	public Property findPropertyByColumn(String column);
}

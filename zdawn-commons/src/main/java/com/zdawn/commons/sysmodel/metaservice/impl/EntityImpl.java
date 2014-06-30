package com.zdawn.commons.sysmodel.metaservice.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.Relation;

public class EntityImpl implements Entity,Serializable {
	private static final long serialVersionUID = -7647843919853215050L;
	private final Logger log = LoggerFactory.getLogger(EntityImpl.class);
	/**
	 * 实体名称 
	 */
	private String name;
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 中文描述 
	 */
	private String description;
	/**
	 * 实体类型
	 */
	private String type;
	/**
	 * 主键属性 
	 */
	private String uniqueColumn;
	/**
	 * 对应java类 可以为空
	 */
	private String clazz;
	/**
	 * 实体关系
	 */
	private List<Relation> relations = new ArrayList<Relation>();
	/**
	 * 属性集合
	 */
	private List<Property> properties = new ArrayList<Property>();
	
	public Property findUniqueColumnProperty(){
		return findPropertyByColumn(uniqueColumn);
	}
	public Property findPropertyByName(String propertyName){
		for (Property property : properties) {
			if(property.isUsing() && 
				propertyName.equalsIgnoreCase(property.getName())) return property;
		}
		return null;
	}
	public Property findPropertyByColumn(String column){
		for (Property property : properties) {
			if(property.isUsing() &&
				column.equalsIgnoreCase(property.getColumn())) return property;
		}
		return null;
	}
	/**
	 * 添加实体关系
	 * @param relation关系对象
	 */
	public void addRelation(Relation relation){
		for (Relation temp : relations) {
			if(temp.equals(relation)) log.warn("实体"+name+" 实体关系重复 "+relation.getDescription());
		}
		relations.add(relation);
	}
	/**
	 * 添加属性
	 * @param property 属性对象
	 */
	public void addProperty(Property property){
		for (Property temp : properties) {
			if(temp.equals(property)) log.warn("实体"+name+" 属性重复 "+property.getName());
		}
		properties.add(property);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	
	public String getUniqueColumn() {
		return uniqueColumn;
	}
	public void setUniqueColumn(String uniqueColumn) {
		this.uniqueColumn = uniqueColumn;
	}
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public List<Relation> getRelations() {
		return relations;
	}
	
	public List<Property> getProperties() {
		return properties;
	}
}

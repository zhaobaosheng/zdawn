package com.zdawn.commons.dict.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 数据字典元数据实体类
 * @author zhaobs
 */
public class MetaDict implements Serializable {
	private static final long serialVersionUID = 840129519273204142L;
	/** 数据字典名称,默认与表名相同*/
	private String dicName = "";
	/**编码表中文描述*/
	private String description = "";
	/**唯一字段名称*/
	private String uniqueField = "";
	/**上级编码关联字段*/
	private String parentField = "";
	/**排序字段*/
	private String orderField = "";
	/**缓存类型,字典数据缓存在哪里 memory mongodb*/
	private String cacheType = "";
	/**数据库表名*/
	private String tableName = "";
	/**过滤条件 不包括where关键字*/
	private String condition = "";
	
	/**字段集合*/
	private List<DataField> listDataFields = new ArrayList<DataField>();
	
	public void addDataField(DataField dataField){
		if(!listDataFields.contains(dataField)){
			listDataFields.add(dataField);
		}
	}
	public void removeDataField(DataField dataField){
		listDataFields.remove(dataField);
	}
	public void removeAll(){
		listDataFields.clear();
	}
	public DataField getUniqueDataField(){
		for (int i = 0; i < listDataFields.size(); i++) {
			DataField temp = listDataFields.get(i);
			if(temp.getFieldName().equals(uniqueField)) return temp;
		}
		return null;
	}
	public DataField getDataFieldByFieldName(String fieldName){
		for (int i = 0; i < listDataFields.size(); i++) {
			DataField temp = listDataFields.get(i);
			if(temp.getFieldName().equals(fieldName)) return temp;
		}
		return null;
	}
	public String getDicName() {
		return dicName;
	}
	public void setDicName(String dicName) {
		this.dicName = dicName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUniqueField() {
		return uniqueField;
	}
	public void setUniqueField(String uniqueField) {
		this.uniqueField = uniqueField;
	}
	public String getParentField() {
		return parentField;
	}
	public void setParentField(String parentField) {
		this.parentField = parentField;
	}
	public String getOrderField() {
		return orderField;
	}
	public String getCacheType() {
		return cacheType;
	}
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}
	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
	public List<DataField> getListDataFields() {
		return listDataFields;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
}

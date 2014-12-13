package com.zdawn.text.lucene.config;

import java.util.ArrayList;
import java.util.List;

public class MetaDocument {
	public static final String SysDocFieldName ="docName";
	/**
	 *  文档英文名字相同索引库唯一
	 */
	private String docName;
	/**
	 * 文档描述
	 */
	private String description;
	/**
	 * 唯一值字段名
	 */
	private String uniqueFieldName;
	/**
	 * 字段集合
	 */
	private List<MetaField> fieldList = new ArrayList<MetaField>();
	/**
	 * 获取file类型字段
	 * @return 如果没有file类型字段返回空List
	 */
	public List<MetaField> getFileField(){
		 List<MetaField> list = new ArrayList<MetaField>();
		 for (MetaField field : fieldList) {
			if(field.getDataType().equals(MetaField.FILE_FIELD)) list.add(field);
		}
		return list;
	}
	
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUniqueFieldName() {
		return uniqueFieldName;
	}
	public void setUniqueFieldName(String uniqueFieldName) {
		this.uniqueFieldName = uniqueFieldName;
	}
	public List<MetaField> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<MetaField> fieldList) {
		this.fieldList = fieldList;
	}
}

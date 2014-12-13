package com.zdawn.text.lucene.config;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.util.NumericUtils;

public class MetaField {
	/**
	 * 字符串
	 */
	public static final String STRING_FIELD = "string";
	/**
	 * int
	 */
	public static final String INT_FIELD = "int";
	/**
	 * long
	 */
	public static final String LONG_FIELD = "long";
	/**
	 * float
	 */
	public static final String FLOAT_FIELD = "float";
	/**
	 * double
	 */
	public static final String DOUBLE_FIELD = "double";
	/**
	 * 文件
	 */
	public static final String FILE_FIELD = "file";
	/**
	 * 字段英文
	 */
	private String fieldName;
	/**
	 * 字段描述
	 */
	private String description;
	/**
	 * 字段类型
	 */
	private String dataType;
	/**
	 * 是否索引
	 */
	private boolean indexed;
	/**
	 * 是否存储
	 */
	private boolean stored;
	/**
	 * 是否分词
	 */
	private boolean tokenized;
	/**
	 * 字段索引和存储信息
	 */
	private FieldType fieldType = null;
	/**
	 * 初始化FieldType
	 */
	public void initFieldType(){
		if(dataType.equals(STRING_FIELD) || dataType.equals(FILE_FIELD)){
			fieldType = new FieldType();
			fieldType.setIndexed(indexed);
			fieldType.setTokenized(tokenized);
			fieldType.setStored(stored);
		    fieldType.freeze();
		}else{
			createNumberFieldType();
		}
	}
	
	private void createNumberFieldType(){
		//忽略indexed和tokenized配置
		fieldType = new FieldType();
		fieldType.setIndexed(true);
		fieldType.setTokenized(true);
		fieldType.setOmitNorms(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_ONLY);
		if(stored) fieldType.setStored(true);
		
		if(dataType.equals(INT_FIELD)){
			fieldType.setNumericType(FieldType.NumericType.INT);
			fieldType.setNumericPrecisionStep(NumericUtils.PRECISION_STEP_DEFAULT_32);
		}else if(dataType.equals(LONG_FIELD)){
			fieldType.setNumericType(FieldType.NumericType.LONG);
		}else if(dataType.equals(FLOAT_FIELD)){
			fieldType.setNumericType(FieldType.NumericType.FLOAT);
			fieldType.setNumericPrecisionStep(NumericUtils.PRECISION_STEP_DEFAULT_32);
		}else if(dataType.equals(DOUBLE_FIELD)){
			fieldType.setNumericType(FieldType.NumericType.DOUBLE);
		}
		fieldType.freeze();
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public void setStored(boolean stored) {
		this.stored = stored;
	}

	public void setTokenized(boolean tokenized) {
		this.tokenized = tokenized;
	}

	public FieldType getFieldType() {
		if(fieldType==null){
			initFieldType();
		}
		return fieldType;
	}
}
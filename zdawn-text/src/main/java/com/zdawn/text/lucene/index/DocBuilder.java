package com.zdawn.text.lucene.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;

import com.zdawn.text.lucene.config.MetaDocument;
import com.zdawn.text.lucene.config.MetaField;

public class DocBuilder {
	public static Document createDocumentExcludeFileField(MetaDocument metaDoc,Map<String, Object> fieldData){
		Document doc = new Document();
		List<Field> fieldList = collectNoneFileField(metaDoc, fieldData);
		doc = createDocument(fieldList);
		return doc;
	}
	public static Document createDocument(List<Field> fieldList){
		Document doc = new Document();
		for (Field field : fieldList) {
			doc.add(field);
		}
		return doc;
	}
	public static List<Field> collectNoneFileField(MetaDocument metaDoc,Map<String, Object> fieldData){
		List<Field> fieldList = new ArrayList<Field>();
		List<MetaField> metaFieldList = metaDoc.getFieldList();
		for (MetaField metaField : metaFieldList) {
			if(metaField.getDataType().equals(MetaField.FILE_FIELD)) continue;
			Object obj = fieldData.get(metaField.getFieldName());
			if(obj==null) continue;
			Field field = createField(metaField, obj);
			if(field!=null) fieldList.add(field);
		}
		//system field
		fieldList.add(new StringField(MetaDocument.SysDocFieldName,metaDoc.getDocName(), Field.Store.YES));
		return fieldList;
	}
	
	public static Field createField(MetaField metaField,Object obj){
		Field field = null;
		if(metaField.getDataType().equals(MetaField.INT_FIELD)){
			if(obj instanceof Integer){
				field = new IntField(metaField.getFieldName(),(Integer)obj,metaField.getFieldType());
			}else{
				String value = obj.toString();
				if(!value.equals("")) field = new IntField(metaField.getFieldName(),Integer.parseInt(value),metaField.getFieldType());
			}
		}else if(metaField.getDataType().equals(MetaField.LONG_FIELD)){
			if(obj instanceof Long){
				field = new LongField(metaField.getFieldName(),(Long)obj,metaField.getFieldType());
			}else{
				String value = obj.toString();
				if(!value.equals("")) field = new LongField(metaField.getFieldName(),Long.parseLong(value),metaField.getFieldType());
			}
		}else if(metaField.getDataType().equals(MetaField.FLOAT_FIELD)){
			if(obj instanceof Float){
				field = new FloatField(metaField.getFieldName(),(Float)obj,metaField.getFieldType());
			}else{
				String value = obj.toString();
				if(!value.equals("")) field = new FloatField(metaField.getFieldName(),Float.parseFloat(value),metaField.getFieldType());
			}
		}else if(metaField.getDataType().equals(MetaField.DOUBLE_FIELD)){
			if(obj instanceof Double){
				field = new DoubleField(metaField.getFieldName(),(Double)obj,metaField.getFieldType());
			}else{
				String value = obj.toString();
				if(!value.equals("")) field = new DoubleField(metaField.getFieldName(),Double.parseDouble(value),metaField.getFieldType());
			}
		}else if(metaField.getDataType().equals(MetaField.STRING_FIELD)){
			String value = obj.toString();
			field = new Field(metaField.getFieldName(),value,metaField.getFieldType());
		}
		return field;
	}
	
	public static Field createFileField(MetaField metaField,String path,String charsetName){
		Field field = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(path),charsetName));
			field = new Field(metaField.getFieldName(),reader,metaField.getFieldType());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("unsupport encoding "+charsetName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file not found "+path);
		}
		return field;
	}
}

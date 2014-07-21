package com.zdawn.commons.dict.model;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DictMetaXMLLoader {
	private final Logger log = LoggerFactory.getLogger(DictMetaXMLLoader.class);
	public MetaDictSet loadFromXML(String filePath) {
		//文件路径
		File xmlfile = new File(filePath);
		if (!xmlfile.exists()) return null;
		MetaDictSet dictSet = new MetaDictSet();
		//创建装载xml对象
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domfactory.newDocumentBuilder();
			Document document = builder.parse(xmlfile);
			NodeList dictList = document.getElementsByTagName("DataDictionary");
			//read DataDictionary element 
			readDataDictionary(dictList,dictSet);
		} catch (Exception e) {
			log.error("load DataDictionarys error: ", e);
		}
		return dictSet.size()==0 ? null:dictSet;
	}
	private void readDataDictionary(NodeList dictList, MetaDictSet dictSet) {
		for (int i = 0; i < dictList.getLength(); i++) {
			Element element = (Element) dictList.item(i);
			MetaDict oneDict = new MetaDict();
			oneDict.setDicName(element.getAttribute("dicName"));
			oneDict.setDescription(element.getAttribute("description"));
			oneDict.setUniqueField(element.getAttribute("uniqueField"));
			oneDict.setParentField(element.getAttribute("parentField"));
			oneDict.setOrderField(element.getAttribute("orderField"));
			String cache = element.getAttribute("cacheType");
			if(cache==null || cache.equals(""))  cache = "memory";
			oneDict.setCacheType(cache);
			String value = element.getAttribute("tableName");
			if(value==null || value.equals(""))  value = oneDict.getDicName();
			oneDict.setTableName(value);
			oneDict.setCondition(element.getAttribute("condition"));
			NodeList dataFieldList = element.getElementsByTagName("DataField");
			//read DataField element
			readDataField(dataFieldList,oneDict);
			dictSet.addMetaDict(oneDict);
		}	
	}
	private void readDataField(NodeList dataFieldList, MetaDict oneDict) {
		for (int i = 0; i < dataFieldList.getLength(); i++) {
			Element element = (Element) dataFieldList.item(i);
			DataField field = new DataField();
			field.setFieldName(element.getAttribute("fieldName"));
			field.setDescription(element.getAttribute("description"));
			field.setDataType(element.getAttribute("dataType"));
			field.setDisplay(Boolean.parseBoolean(element.getAttribute("isDisplay")));
			String toStringformat = element.getAttribute("toStringformat");
			if(toStringformat!=null && !toStringformat.equals("")) field.setToStringformat(toStringformat);
			oneDict.addDataField(field);
		}
	}
}

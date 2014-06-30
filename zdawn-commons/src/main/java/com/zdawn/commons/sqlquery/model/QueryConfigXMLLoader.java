package com.zdawn.commons.sqlquery.model;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 从xml文件加载查询模型
 * @author zhaobs
 *
 */
public class QueryConfigXMLLoader {
	private final Logger log = LoggerFactory.getLogger(QueryConfigXMLLoader.class);
	
	public MetaQuery loadFromXML(String filePath) {
		//文件路径
		File xmlfile = new File(filePath);
		if (!xmlfile.exists()) return null;
		return loadFromXML(xmlfile);
	}
	public MetaQuery loadFromXML(File file) {
		MetaQuery metaQuery = new MetaQuery();
		//创建装载xml对象
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domfactory.newDocumentBuilder();
			Document document = builder.parse(file);
			//read version
			NodeList version = document.getElementsByTagName("version");
			if(version.getLength()==1){
				Element element =(Element)version.item(0);
				metaQuery.setVersion(element.getFirstChild().getNodeValue());
			}
			//read ResultMapper element
			NodeList resultMapperList = document.getElementsByTagName("ResultMapper");
			readResultMapper(resultMapperList,metaQuery);
			//read QueryConfig element
			NodeList queryConfigList = document.getElementsByTagName("QueryConfig");
			readQueryConfig(queryConfigList,metaQuery);
		} catch (Exception e) {
			log.error("load sql-query error:", e);
		}
		return metaQuery;
	}

	private void readQueryConfig(NodeList queryConfigList, MetaQuery metaQuery) {
		if(queryConfigList==null) return;
		for (int i = 0; i < queryConfigList.getLength(); i++) {
			Element element = (Element) queryConfigList.item(i);
			QueryConfig queryConfig = new QueryConfig();
			queryConfig.setCode(element.getAttribute("code"));
			queryConfig.setName(element.getAttribute("name"));
			queryConfig.setPaging(Boolean.parseBoolean(element.getAttribute("paging")));
			queryConfig.setResultMapper(element.getAttribute("resultMapper"));
			//read SelectSql
			NodeList list = element.getElementsByTagName("SelectSql");
			if(list.getLength()!=1) throw new RuntimeException("SelectSql Node error!");
			Element temp =(Element)list.item(0);
			queryConfig.setSelectSql(temp.getFirstChild().getNodeValue());
			//read CountSql
			list = element.getElementsByTagName("CountSql");
			if(list.getLength()!=1) throw new RuntimeException("CountSql Node error!");
			temp =(Element)list.item(0);
			queryConfig.setCountSql(temp.getFirstChild().getNodeValue());
			//read ParameterMapper
			readParameterMapper(element,queryConfig);
			metaQuery.addQueryConfig(queryConfig);
		}
	}

	private void readParameterMapper(Element configElement, QueryConfig queryConfig) {
		ParameterMapper pm = new ParameterMapper();
		queryConfig.setParameterMapper(pm);
		NodeList parameterList = configElement.getElementsByTagName("parameter");
		if(parameterList==null || parameterList.getLength()==0) return ;
		for (int i = 0; i < parameterList.getLength(); i++) {
			Element element = (Element) parameterList.item(i);
			ParameterItem item = new ParameterItem();
			item.setColumn(element.getAttribute("column"));
			item.setProperty(element.getAttribute("property"));
			item.setType(element.getAttribute("type"));
			item.setToStringformat(element.getAttribute("toStringformat"));
			String empty = element.getAttribute("empty");
			if(empty!=null && !empty.equals("")) item.setEmpty(Boolean.parseBoolean(empty));
			pm.addParameterItem(item);
		}
	}

	private void readResultMapper(NodeList resultMapperList, MetaQuery metaQuery) {
		if(resultMapperList==null) return;
		for (int i = 0; i < resultMapperList.getLength(); i++) {
			Element element = (Element) resultMapperList.item(i);
			ResultMapper resultMapper = new ResultMapper();
			resultMapper.setId(element.getAttribute("id"));
			resultMapper.setType(element.getAttribute("type"));
			NodeList resultList = element.getElementsByTagName("result");
			if(resultList!=null && resultList.getLength()>0){
				for (int j = 0; j < resultList.getLength(); j++) {
					Element resultElement = (Element) resultList.item(j);
					ResultItem item = new ResultItem();
					item.setColumn(resultElement.getAttribute("column"));
					item.setProperty(resultElement.getAttribute("property"));
					item.setType(resultElement.getAttribute("type"));
					item.setToStringformat(resultElement.getAttribute("toStringformat"));
					resultMapper.addResultItem(item);
				}
			}
			metaQuery.addResultMapper(resultMapper);
		}
		
	}
}

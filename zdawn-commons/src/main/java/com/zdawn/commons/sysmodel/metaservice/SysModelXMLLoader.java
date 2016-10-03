package com.zdawn.commons.sysmodel.metaservice;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.zdawn.commons.sysmodel.metaservice.impl.EntityImpl;
import com.zdawn.commons.sysmodel.metaservice.impl.PropertyImpl;
import com.zdawn.commons.sysmodel.metaservice.impl.ReferenceImpl;
import com.zdawn.commons.sysmodel.metaservice.impl.RelationImpl;
import com.zdawn.commons.sysmodel.metaservice.impl.SysModelImpl;
import com.zdawn.util.Utils;
import com.zdawn.util.convert.ConvertUtil;

/**
 * 从xml文件加载实体模型
 * @author zhaobs
 *
 */
public class SysModelXMLLoader {
	private final Logger log = LoggerFactory.getLogger(SysModelXMLLoader.class);
	
	public SysModel loadFromXML(String filePath) {
		//文件路径
		File xmlfile = new File(filePath);
		if (!xmlfile.exists()) return null;
		return loadFromXML(xmlfile);
	}
	public SysModel loadFromXML(File file) {
		SysModelImpl sysModel = new SysModelImpl();
		//创建装载xml对象
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		try {
			log.info("load sysmodel xml="+file.getAbsolutePath());
			DocumentBuilder builder = domfactory.newDocumentBuilder();
			Document document = builder.parse(file);
			//read version
			NodeList version = document.getElementsByTagName("version");
			if(version.getLength()==1){
				Element element =(Element)version.item(0);
				sysModel.setVersion(element.getFirstChild().getNodeValue());
			}
			//read config
			NodeList resultList = document.getElementsByTagName("config");
			readConfig(resultList,sysModel);
			//read Entity
			resultList = document.getElementsByTagName("Entity");
			readEntity(resultList,sysModel);
			sysModel.initAndValidateEntityInfo();
		} catch (Exception e) {
			log.error("load sysmodel error:", e);
		}
		return sysModel;
	}

	private void readEntity(NodeList resultList, SysModelImpl sysModel) {
		if(resultList==null) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element entityElement = (Element) resultList.item(i);
			//read entity attribute
			EntityImpl entity = new EntityImpl();
			entity.setTableName(entityElement.getAttribute("tableName"));
			String temp = entityElement.getAttribute("name");
			if(Utils.isEmpty(temp)) temp = Utils.convertStandardName(entity.getTableName(),false);
			entity.setName(temp);
			entity.setDescription(entityElement.getAttribute("description"));
			entity.setType(entityElement.getAttribute("type"));
			entity.setClazz(entityElement.getAttribute("clazz"));
			temp = entityElement.getAttribute("uniqueColumn");
			if(Utils.isEmpty(temp)) throw new RuntimeException("实体"+entity.getDescription()+"uniqueColumn属性为空");
			entity.setUniqueColumn(temp);
			//read property
			readProperty(entityElement.getElementsByTagName("property"),entity);
			//read relation
			readRelation(entityElement.getElementsByTagName("relation"),entity);
			sysModel.addEntity(entity);
		}
	}
	private void readRelation(NodeList resultList, EntityImpl entity) {
		if(resultList==null || resultList.getLength()==0) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element relationElement = (Element) resultList.item(i);
			RelationImpl relation = new RelationImpl();
			relation.setSelfPropertyName(relationElement.getAttribute("selfPropertyName"));
			relation.setSelfColumn(relationElement.getAttribute("selfColumn"));
			relation.setDescription(relationElement.getAttribute("description"));
			relation.setEntityName(relationElement.getAttribute("entityName"));
			relation.setTableName(relationElement.getAttribute("tableName"));
			relation.setPropertyName(relationElement.getAttribute("propertyName"));
			relation.setColumn(relationElement.getAttribute("column"));
			relation.setType(relationElement.getAttribute("type"));
			if(Utils.isEmpty(relation.getSelfPropertyName()) 
					&& Utils.isEmpty(relation.getSelfColumn())){
				relation.setSelfColumn(entity.getUniqueColumn());
			}
			if(Utils.isEmpty(relation.getEntityName()) 
					&& Utils.isEmpty(relation.getTableName())){
				throw new RuntimeException("实体关系"+relation.getDescription()+"关联实体配置不正确");
			}
			if(Utils.isEmpty(relation.getPropertyName()) 
					&& Utils.isEmpty(relation.getColumn())){
				throw new RuntimeException("实体关系"+relation.getDescription()+"关联属性配置不正确");
			}
			entity.addRelation(relation);
		}
	}
	private void readProperty(NodeList resultList, EntityImpl entity) {
		if(resultList==null) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element propertyElement = (Element) resultList.item(i);
			PropertyImpl property = new PropertyImpl();
			//read attribute
			property.setColumn(propertyElement.getAttribute("column"));
			String temp = propertyElement.getAttribute("name");
			if(Utils.isEmpty(temp)) temp = Utils.convertStandardName(property.getColumn(),true);
			property.setName(temp);
			Boolean using = ConvertUtil.convertBoolean(propertyElement.getAttribute("using"));
			property.setUsing(using==null ? true:using);
			property.setDescription(propertyElement.getAttribute("description"));
			property.setType(propertyElement.getAttribute("type"));
			temp = propertyElement.getAttribute("length");
			if(!Utils.isEmpty(temp)) property.setLength(ConvertUtil.convertInteger(temp));
			temp = propertyElement.getAttribute("scale");
			if(!Utils.isEmpty(temp)) property.setScale(ConvertUtil.convertInteger(temp));
			temp = propertyElement.getAttribute("notNull");
			if(!Utils.isEmpty(temp)) property.setNotNull(ConvertUtil.convertBoolean(temp));
			property.setDefaultValue(propertyElement.getAttribute("defaultValue"));
			property.setToStringformat(propertyElement.getAttribute("toStringformat"));
			//read reference
			readReference(propertyElement.getElementsByTagName("reference"),property);
			entity.addProperty(property);
		}
	}
	private void readReference(NodeList resultList, PropertyImpl property) {
		if(resultList==null || resultList.getLength()==0) return ;
		Element referenceElement = (Element) resultList.item(0);
		ReferenceImpl reference = new ReferenceImpl();
		reference.setEntityName(referenceElement.getAttribute("entityName"));
		reference.setTableName(referenceElement.getAttribute("tableName"));
		reference.setPropertyName(referenceElement.getAttribute("propertyName"));
		reference.setColumn(referenceElement.getAttribute("column"));
		reference.setType(referenceElement.getAttribute("type"));
		reference.setDisplayPropertyName(referenceElement.getAttribute("displayPropertyName"));
		reference.setDisplayColumn(referenceElement.getAttribute("displayColumn"));
		if(Utils.isEmpty(reference.getEntityName()) 
				&& Utils.isEmpty(reference.getTableName())){
			throw new RuntimeException("属性"+property.getName()+"引用实体配置不正确");
		}
		if(Utils.isEmpty(reference.getPropertyName()) 
				&& Utils.isEmpty(reference.getColumn())){
			throw new RuntimeException("属性"+property.getName()+"引用属性配置不正确");
		}
		if(Utils.isEmpty(reference.getDisplayPropertyName()) 
				&& Utils.isEmpty(reference.getDisplayColumn())){
			throw new RuntimeException("属性"+property.getName()+"显示属性配置不正确");
		}
		property.setReference(reference);
	}
	private void readConfig(NodeList resultList, SysModelImpl sysModel) {
		if(resultList==null) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element element = (Element) resultList.item(i);
			NodeList propertyList = element.getElementsByTagName("property");
			if(propertyList==null) continue;
			for (int j = 0; j < propertyList.getLength(); j++) {
				Element propertyElement = (Element) propertyList.item(j);
				sysModel.addConfig(propertyElement.getAttribute("name"),
								   propertyElement.getAttribute("value"));
			}
		}
	}
}

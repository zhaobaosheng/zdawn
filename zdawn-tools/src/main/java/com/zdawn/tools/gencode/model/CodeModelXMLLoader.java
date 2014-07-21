package com.zdawn.tools.gencode.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zdawn.util.Utils;
import com.zdawn.util.validator.ValidatorUtil;

/**
 * 从xml文件加载实体模型
 * @author zhaobs
 *
 */
public class CodeModelXMLLoader {
	private final Logger log = LoggerFactory.getLogger(CodeModelXMLLoader.class);
	
	public CodeModel loadFromXML(String filePath) {
		//文件路径
		File xmlfile = new File(filePath);
		if (!xmlfile.exists()) return null;
		return loadFromXML(xmlfile);
	}
	public CodeModel loadFromXML(File file) {
		CodeModel codeModel = new CodeModel();
		//创建装载xml对象
		DocumentBuilderFactory domfactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domfactory.newDocumentBuilder();
			Document document = builder.parse(file);
			//read config
			NodeList resultList = document.getElementsByTagName("config");
			readConfig(resultList,codeModel);
			//read importEntity
			resultList = document.getElementsByTagName("importEntity");
			readImportEntity(resultList,codeModel);
			//read MetaClazz
			resultList = document.getElementsByTagName("MetaClazz");
			readMetaClazz(resultList,codeModel);
		} catch (Exception e) {
			log.error("load codemodel error:", e);
		}
		return codeModel;
	}

	private void readMetaClazz(NodeList resultList, CodeModel codeModel) {
		if(resultList==null) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element clazzElement = (Element) resultList.item(i);
			MetaClazz clazz = new MetaClazz();
			clazz.setPkg(clazzElement.getAttribute("pkg"));
			clazz.setNameCn(clazzElement.getAttribute("nameCn"));
			clazz.setName(clazzElement.getAttribute("name"));
			if(Utils.isEmpty(clazz.getPkg()) || Utils.isEmpty(clazz.getName())) 
					throw new RuntimeException("包名pkg、类名name，不能为空！");
			String[] result = ValidatorUtil.validateChStr(clazz.getPkg(), true, "包名",null);
			if(result[0].equals("true")) throw new RuntimeException ("包名pkg有中文");
			result = ValidatorUtil.validateChStr(clazz.getName(), true, "类名name",null);
			if(result[0].equals("true")) throw new RuntimeException ("类名name有中文");
			//read Method
			readMethod(clazzElement.getElementsByTagName("Method"),clazz);
			codeModel.addMetaClazz(clazz);
		}
	}
	private void readMethod(NodeList resultList, MetaClazz clazz) {
		if(resultList==null) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element methodElement = (Element) resultList.item(i);
			Method method = new Method();
			method.setName(methodElement.getAttribute("name"));
			method.setNameCn(methodElement.getAttribute("nameCn"));
			method.setType(methodElement.getAttribute("type"));
			method.setGenServiceClazz(Boolean.parseBoolean(methodElement.getAttribute("genServiceClazz")));
			method.setUsingClazz(Boolean.parseBoolean(methodElement.getAttribute("usingClazz")));
			//read HandleEntity
			readHandleEntity(methodElement.getElementsByTagName("HandleEntity"),method);
			//read validateRules
			readValidateRule(methodElement.getElementsByTagName("validateRules"),method);
			clazz.addMethod(method);
		}
	}
	private void readValidateRule(NodeList resultList, Method method) {
		if(resultList==null || resultList.getLength()==0) return ;
		List<ValidateRule> list = new ArrayList<ValidateRule>();
		Element ruleElement = (Element) resultList.item(0);
		NodeList childNode = ruleElement.getChildNodes();
		for (int i = 0; i < childNode.getLength(); i++) {
			Node node = childNode.item(i);
			if(node instanceof Element){
				Element tmp = (Element) node;
				String type = tmp.getAttribute("type");
				if(type.equals("rule")){
					ValidateRule base = new ValidateRule();
					readValidateRule(base,tmp);
					if(!base.getRequire().equals("true"))  throw new RuntimeException ("必填验证require属性必须为true");
					list.add(base);
				}else if(type.equals("lengthRule")){
					LengthRule rule = new LengthRule();
					readValidateRule(rule,tmp);
					rule.setMaxLength(tmp.getAttribute("maxLength"));
					rule.setMinLength(tmp.getAttribute("minLength"));
					if(Utils.isEmpty(rule.getMaxLength()) 
							&& Utils.isEmpty(rule.getMinLength())){
						throw new RuntimeException("最大、最小长度不能同时为空");
					}
					list.add(rule);
				}else if(type.equals("regxRule")){
					RegxRule rule = new RegxRule();
					readValidateRule(rule,tmp);
					rule.setExpression(tmp.getAttribute("expression"));
					if(Utils.isEmpty(rule.getExpression())) throw new RuntimeException("正则表达式为空");
					list.add(rule);
				}else if(type.equals("exceptRule")){
					ExceptRule rule = new ExceptRule();
					readValidateRule(rule,tmp);
					rule.setCodeDicName(tmp.getAttribute("codeDicName"));
					if(Utils.isEmpty(rule.getCodeDicName())) rule.setContent(tmp.getTextContent());
					if(Utils.isEmpty(rule.getCodeDicName()) 
							&& Utils.isEmpty(rule.getContent())){
						throw new RuntimeException("编码数据字典名称和编码内容不能同时为空");
					}
					list.add(rule);
				}
			}
		}
		if(list.size()>0) method.setValidators(list);
	}
	private ValidateRule readValidateRule(ValidateRule base,Element ruleElement) {
		base.setEntityName(ruleElement.getAttribute("entityName"));
		base.setTableName(ruleElement.getAttribute("tableName"));
		base.setPropertyName(ruleElement.getAttribute("propertyName"));
		base.setColumn(ruleElement.getAttribute("column"));
		base.setErrorMessage(ruleElement.getAttribute("errorMessage"));
		base.setRequire(ruleElement.getAttribute("require"));
		if(Utils.isEmpty(base.getEntityName()) 
				&& Utils.isEmpty(base.getTableName())){
			throw new RuntimeException("实体名和表名不能同时为空");
		}
		if(Utils.isEmpty(base.getPropertyName()) && Utils.isEmpty(base.getColumn())){
			throw new RuntimeException("属性名和字段名不能同时为空");
		}
		return base;
	}
	private void readHandleEntity(NodeList resultList, Method method) {
		if(resultList==null || resultList.getLength()==0) throw new RuntimeException ("方法没有处理实体");
		boolean main = false;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element handleElement = (Element) resultList.item(i);
			HandleEntity handleEntity =  new HandleEntity();
			handleEntity.setName(handleElement.getAttribute("name"));
			handleEntity.setTableName(handleElement.getAttribute("tableName"));
			if(Utils.isEmpty(handleEntity.getName()) 
					&& Utils.isEmpty(handleEntity.getTableName())){
				throw new RuntimeException("实体名和表名不能同时为空");
			}
			handleEntity.setMain(Boolean.parseBoolean(handleElement.getAttribute("main")));
			if(handleEntity.isMain()) main = true;
			//read DataItem
			readDataItem(handleElement.getElementsByTagName("DataItem"),handleEntity);
			method.addHandleEntity(handleEntity);
		}
		if(!main) throw new RuntimeException("没有配置主实体");
	}
	private void readDataItem(NodeList resultList,HandleEntity handleEntity) {
		if(resultList==null || resultList.getLength()==0) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element itemElement = (Element) resultList.item(i);
			String propertyName = itemElement.getAttribute("propertyName");
			String column = itemElement.getAttribute("column");
			if(Utils.isEmpty(propertyName) && Utils.isEmpty(column)){
				throw new RuntimeException("属性名和字段名不能同时为空");
			}
			handleEntity.addDataItem(new String[]{propertyName,column});
		}
	}
	private void readImportEntity(NodeList resultList, CodeModel codeModel) {
		if(resultList==null) return ;
		for (int i = 0; i < resultList.getLength(); i++) {
			Element importElement = (Element) resultList.item(i);
			String resource = importElement.getAttribute("resource");
			if(resource!=null && !resource.equals("")) codeModel.addImportEntity(resource);
		}
	}
	private void readConfig(NodeList resultList, CodeModel codeModel) {
		if(resultList==null || resultList.getLength()==0) return ;
		Element elementConfig = (Element) resultList.item(0);
		Config config = new Config();
		//read version
		NodeList tempList = elementConfig.getElementsByTagName("version");
		if(tempList.getLength()==1){
			Element temp =(Element)tempList.item(0);
			config.setVersion(temp.getFirstChild().getNodeValue());
		}
		//generatePath
		tempList = elementConfig.getElementsByTagName("generatePath");
		if(tempList.getLength()==0) throw new RuntimeException("生成路径不能为空");
		Element temp =(Element)tempList.item(0);
		config.setGeneratePath(temp.getFirstChild().getNodeValue());
		//packagePrefix
		tempList = elementConfig.getElementsByTagName("packagePrefix");
		if(tempList.getLength()==0) throw new RuntimeException("包路径不能为空");
		temp =(Element)tempList.item(0);
		config.setPackagePrefix(temp.getFirstChild().getNodeValue());
		codeModel.setConfig(config);
	}
}

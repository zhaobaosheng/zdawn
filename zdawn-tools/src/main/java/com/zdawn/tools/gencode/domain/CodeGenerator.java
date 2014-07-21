package com.zdawn.tools.gencode.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.Relation;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.tools.gencode.model.CodeModel;
import com.zdawn.tools.gencode.model.CodeModelXMLLoader;
import com.zdawn.tools.gencode.model.HandleEntity;
import com.zdawn.tools.gencode.model.MetaClazz;
import com.zdawn.tools.gencode.model.Method;
import com.zdawn.tools.gencode.model.ValidateRule;
import com.zdawn.util.ResourceUtil;
import com.zdawn.util.Utils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class CodeGenerator {
	
	private final Logger log = LoggerFactory.getLogger(CodeGenerator.class);
	
	private String templates = "templates";
	
	private Configuration cfg = null;
	
	private CodeModel codeModel = null;
	
	private SysModel  sysModel = null;
	
	public void initCodeModelFromClassPath(String fileName){
		URL url = this.getClass().getClassLoader().getResource("");
		ArrayList<File> list = ResourceUtil.findFiles(url.getPath(),fileName,true);
		if(list.size()==0) throw new RuntimeException(fileName+" not exist!");
		initCodeModel(list.get(0));
	}
	public void initCodeModel(File file){
		CodeModelXMLLoader xmlLoader = new CodeModelXMLLoader();
		codeModel = xmlLoader.loadFromXML(file);
		if(codeModel!=null){
			SysModelReader reader = new SysModelReader();
			sysModel = reader.readSysModel(codeModel);
			//init method
			initCodeModelDataBySysModel();
		}
	}
	private void initCodeModelDataBySysModel() {
		if(codeModel==null || sysModel==null) return;
		List<MetaClazz> list = codeModel.getMetaClazzes();
		for (MetaClazz metaClazz : list) {
			List<Method> methodList = metaClazz.getMethods();
			for (Method method : methodList) {
				List<HandleEntity> methodHandleEntity = method.getHandleEntities();
				for (HandleEntity handleEntity : methodHandleEntity) {
					Entity entity = null;
					if(Utils.isEmpty(handleEntity.getName())){
						entity = sysModel.findEntityByTableName(handleEntity.getTableName());
						if(entity==null) throw new RuntimeException("entity not find tableName="+handleEntity.getTableName());
						handleEntity.setName(entity.getName());
					}else{
						entity = sysModel.findEntityByName(handleEntity.getName());
						if(entity==null) throw new RuntimeException("entity not find name="+handleEntity.getName());
						handleEntity.setTableName(entity.getTableName());
					}
					initDataItems(entity,handleEntity);
				}
				List<ValidateRule>methodValidateRule = method.getValidators();
				List<String> entityNameList = new ArrayList<String>();
				List<String> tableNameList = new ArrayList<String>();
				collectEntityNames(methodValidateRule,entityNameList,tableNameList);
				//entityNameList
				for (int i = 0; i < entityNameList.size(); i++) {
					Entity entity =sysModel.findEntityByName(entityNameList.get(i));
					if(entity==null) throw new RuntimeException("entity not find tableName="+entityNameList.get(i));
					initValidateRule(entity,methodValidateRule);
				}
				//tableNameList
				for (int i = 0; i < tableNameList.size(); i++) {
					Entity entity =sysModel.findEntityByTableName(tableNameList.get(i));
					if(entity==null) throw new RuntimeException("entity not find tableName="+tableNameList.get(i));
					initValidateRule(entity,methodValidateRule);
				}
			}
		}
	}
	private void initValidateRule(Entity entity,
			List<ValidateRule> methodValidateRule) {
		for (ValidateRule validateRule : methodValidateRule) {
			if(validateRule.getEntityName().equals(entity.getName())){
				validateRule.setTableName(entity.getTableName());
			}else if(validateRule.getTableName().equals(entity.getTableName())){
				validateRule.setEntityName(entity.getName());
			}else{
				continue;
			}
			if(Utils.isEmpty(validateRule.getPropertyName())){
				Property property = entity.findPropertyByColumn(validateRule.getColumn());
				if(property==null) throw new RuntimeException("validateRule column="+validateRule.getColumn()+" not found in "+entity.getName());
				validateRule.setPropertyName(property.getName());
			}else{
				Property property = entity.findPropertyByName(validateRule.getPropertyName());
				if(property==null) throw new RuntimeException("validateRule propertyName="+validateRule.getPropertyName()+" not found in "+entity.getName());
				validateRule.setColumn(property.getColumn());
			}
		}
	}
	private void collectEntityNames(List<ValidateRule> methodValidateRule,
			List<String> entityNameList, List<String> tableNameList) {
		for (ValidateRule validateRule : methodValidateRule) {
			if(validateRule.getEntityName().length()>0 && !entityNameList.contains(validateRule.getEntityName())){
				entityNameList.add(validateRule.getEntityName());
			}else if(!tableNameList.contains(validateRule.getTableName())){
				tableNameList.add(validateRule.getTableName());
			}
		}
	}
	//初始化属性名
	private void initDataItems(Entity entity, HandleEntity handleEntity) {
		List<String[]> dataItems = handleEntity.getDataItems();
		if(dataItems==null || dataItems.size()==0) return ;
		for (String[] temp : dataItems) {
			if(Utils.isEmpty(temp[0])){
				Property property = entity.findPropertyByColumn(temp[1]);
				if(property==null) throw new RuntimeException("data item column="+temp[1]+" not found in "+entity.getName());
				temp[0] = property.getName();
			}else{
				Property property = entity.findPropertyByName(temp[0]);
				if(property==null) throw new RuntimeException("data item name="+temp[0]+" not found in "+entity.getName());
				temp[1] = property.getColumn();
			}
		}
	}
	public void initConfiguration(){
		try {
			URL url = CodeGenerator.class.getClassLoader().getResource(templates);
			cfg = new Configuration();
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			cfg.setBooleanFormat("true,false");
			cfg.setDirectoryForTemplateLoading(new File(url.getPath()));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setOutputEncoding("UTF-8");
		} catch (IOException e) {
			log.error("初始化模板失败",e);
		}
	}
	
	private void generateCode(String templatePath,String saveFilePath,Map<String,Object> data){
		Writer writer = null;
		try {	
			FileOutputStream fos = new FileOutputStream(saveFilePath);
			writer = new OutputStreamWriter(fos,"UTF-8");
			// 获取指定模板文件
			Template template = cfg.getTemplate(templatePath);
			//开始生成
			template.process(data, writer);
		} catch (Exception e) {
			log.error("generateCode",e);
		}finally{
			try {
				if(writer!=null) writer.close();
			} catch (IOException e) {}
		}
	}
	
	public void generateCodeByMetaClazz(MetaClazz metaClazz){
		String savePath = codeModel.getConfig().getGeneratePath();
		String packagePath = codeModel.getConfig().getPackagePrefix()+'.'+metaClazz.getPkg();
		String clazzParentPath = savePath+'/'+packagePath.replace('.', '/');
		File tempFile = new File(clazzParentPath);
		if(!tempFile.exists() && !tempFile.mkdirs()){
			log.error("创建目录失败  path="+clazzParentPath);
			return ;
		}
		//生成metaclass涉及的实体
		generateClazzEntity(metaClazz,packagePath,clazzParentPath);
		//生成业务逻辑类
		generateClazzDomain(metaClazz,packagePath,clazzParentPath);
		//生成接入服务类
		generateClazzService(metaClazz,packagePath,clazzParentPath);
	}
	/***
	 * 接入服务类
	 * @param metaClazz 类名
	 * @param packagePath package-模块  packagePrefix + pkg
	 * @param clazzParentPath 文件基路径-模块
	 */
	private void generateClazzService(MetaClazz metaClazz, String packagePath,
			String clazzParentPath) {
		Map<String, Object> methodEntityMap = findMethodEntitys(metaClazz);
		//准备模型
		String clazzPath = clazzParentPath + "/service/";
		File tempFile = new File(clazzPath);
		if(!tempFile.exists() && !tempFile.mkdirs()){
			log.error("创建目录失败  path="+clazzPath);
			return ;
		}
		Map<String,Object> root = new HashMap<String, Object>();
		root.put("packagePath", packagePath);
		root.put("package", packagePath+".service");
		root.put("packagefordomain", packagePath+".domain");  //需要引入的domain包
		root.put("methodEntityMap", methodEntityMap);			//HandleEntity
		root.put("metaClazz", metaClazz);									//MetaClazz
		String saveFilePath = clazzPath + metaClazz.getName()+"Service.java";
		generateCode("code/web/implementation.service", saveFilePath, root);
	}

	/**
	 * 生成业务逻辑
	 * @param metaClazz 类名
	 * @param packagePath package-模块  packagePrefix + pkg
	 * @param clazzParentPath 文件基路径-模块
	 */
	private void generateClazzDomain(MetaClazz metaClazz, String packagePath,
			String clazzParentPath) {
		Map<String, Object> methodEntityMap = findMethodEntitys(metaClazz);
		//准备模型
		String clazzPath = clazzParentPath + "/domain/";
		File tempFile = new File(clazzPath);
		if(!tempFile.exists() && !tempFile.mkdirs()){
			log.error("创建目录失败  path="+clazzPath);
			return ;
		}
		Map<String,Object> root = new HashMap<String, Object>();
		root.put("package", packagePath+".domain");
		root.put("packagePath", packagePath);
		root.put("methodEntityMap", methodEntityMap);
		root.put("metaClazz", metaClazz);
		root.put("customParameters",ResourceUtil.readPropertyFileFromClassPath("custom_parameters.properties"));
		String saveFilePath = clazzPath + metaClazz.getName()+".java";
		generateCode("code/domain/interface.domain", saveFilePath, root);
		saveFilePath = clazzPath + metaClazz.getName()+"Impl.java";
		generateCode("code/domain/implementation.domain", saveFilePath, root);
	}
	//查找每个方法使用实体
	private Map<String, Object> findMethodEntitys(MetaClazz metaClazz) {
		Map<String,Object> methodEntityMap = new HashMap<String, Object>();
		//使用实体类当前实体集合
		List<Entity> nonceClazzEntityList = new ArrayList<Entity>();
		//使用实体类其他实体
		List<Entity> otherClazzEntityList = new ArrayList<Entity>();
		List<Method> methodList = metaClazz.getMethods();
		for (Method method : methodList) {
			List<HandleEntity> list = method.getHandleEntities();
			Entity nonceEntity = null;
			for (HandleEntity handleEntity : list) {
				Entity entity = sysModel.findEntityByName(handleEntity.getName());
				//主实体
				if(handleEntity.isMain()){
					nonceEntity = entity;
					methodEntityMap.put(method.getName()+"nonceEntity",nonceEntity);
					if(method.isUsingClazz() && !nonceClazzEntityList.contains(nonceEntity)) nonceClazzEntityList.add(nonceEntity);
				}else{
					if(method.isUsingClazz() && !otherClazzEntityList.contains(entity)) otherClazzEntityList.add(entity); 
				}
			}
			//oneToOne实体
			ArrayList<Entity> aboutEntityList = new ArrayList<Entity>();
			//oneToMany实体
			ArrayList<Entity> subEntityList = new ArrayList<Entity>();
			if(nonceEntity!=null && nonceEntity.getRelations()!=null){
				List<Relation> relationList = nonceEntity.getRelations();
				for (Relation relation : relationList) {
					Entity temp = null;
					if(Utils.isEmpty(relation.getEntityName())){
						temp = sysModel.findEntityByTableName(relation.getTableName());
					}else{
						temp = sysModel.findEntityByName(relation.getEntityName());
					}
					if(relation.getType().equals("oneToOne")){
						if(temp!=null) aboutEntityList.add(temp);
					}else if(relation.getType().equals("oneToMany")){
						if(temp!=null)  subEntityList.add(temp);
					}
				}
			}
			methodEntityMap.put(method.getName()+"aboutEntityList",aboutEntityList);
			methodEntityMap.put(method.getName()+"subEntityList",subEntityList);
		}
		methodEntityMap.put("nonceClazzEntityList", nonceClazzEntityList);
		methodEntityMap.put("otherClazzEntityList", otherClazzEntityList);
		return methodEntityMap;
	}
	
	private Entity getEntitiyByRelation(Relation relation,List<Entity> otherEntityList) {
		for (Entity entity : otherEntityList) {
			if(relation.getEntityName().equals(entity.getName())) return entity;
			if(relation.getTableName().equals(entity.getTableName())) return entity;
		}
		return null;
	}
	private void generateClazzEntity(MetaClazz metaClazz, String packagePath,
			String clazzParentPath) {
		//找到类中需要生成实体
		List<Entity> entityList = new ArrayList<Entity>();
		List<Method> methodList = metaClazz.getMethods();
		for (Method method : methodList) {
			//不需要实体不生成类
			if(!method.isUsingClazz()) continue;
			List<HandleEntity> list = method.getHandleEntities();
			for (HandleEntity handleEntity : list) {
				Entity entity = null;
				if(Utils.isEmpty(handleEntity.getName())){
					entity = sysModel.findEntityByTableName(handleEntity.getTableName());
					if(entity==null) throw new RuntimeException("entity not find tableName="+handleEntity.getTableName());
				}else{
					entity = sysModel.findEntityByName(handleEntity.getName());
					if(entity==null) throw new RuntimeException("entity not find name="+handleEntity.getName());
				}
				if(!entityList.contains(entity)) entityList.add(entity);
			}
		}
		//准备模型
		Map<String,Object> root = new HashMap<String, Object>();
		root.put("clazzPropertyMapping",ResourceUtil.readPropertyFileFromClassPath("clazz_property_mapping.properties") );
		boolean first = true;
		String clazzPath = null;
		String entityName = null;
		for (Entity entity : entityList) {
			if(entity.getClazz().equals("")){
				if(first){
					clazzPath = clazzParentPath + "/dao/model/";
					File tempFile = new File(clazzPath);
					if(!tempFile.exists() && !tempFile.mkdirs()){
						log.error("创建目录失败  path="+clazzPath);
						continue ;
					}
					first = false;
				}
				entityName = entity.getName();
				root.put("package", packagePath+".dao.model");
			}else{
				int index = entity.getClazz().lastIndexOf('.');
				String pack = entity.getClazz().substring(0,index);
				entityName = entity.getClazz().substring(index+1);
				String entityPath = codeModel.getConfig().getGeneratePath();
				entityPath = entityPath.endsWith("/") ? entityPath+pack.replace('.','/'):entityPath+'/'+pack.replace('.','/');
				entityPath = entityPath +'/';
				File tempFile = new File(entityPath);
				if(!tempFile.exists() && !tempFile.mkdirs()){
					log.error("创建目录失败  path="+entityPath);
					continue;
				}
				clazzPath = entityPath;
				root.put("package", pack);
			}
			String saveFilePath = clazzPath + entityName+".java";
			root.put("entity", entity);
			//oneToOne实体
			ArrayList<Entity> aboutEntityList = new ArrayList<Entity>();
			//oneToMany实体
			ArrayList<Entity> subEntityList = new ArrayList<Entity>();
			root.put("aboutEntityList", aboutEntityList);
			root.put("subEntityList", subEntityList);
			List<Relation> relationList = entity.getRelations();
			if(relationList!=null){
				for (Relation relation : relationList) {
					if(relation.getType().equals("oneToOne")){
						Entity temp = getEntitiyByRelation(relation,entityList);
						if(temp!=null) aboutEntityList.add(temp);
					}else if(relation.getType().equals("oneToMany")){
						Entity temp = getEntitiyByRelation(relation,entityList);
						if(temp!=null)  subEntityList.add(temp);
					}
				}
			}
			//gen pojo class
			generateCode("code/entity/pojo.entity", saveFilePath, root);
		}
	}

	public void generateCodeAllClazz(){
		List<MetaClazz> metaClazzList = codeModel.getMetaClazzes();
		for (MetaClazz metaClazz : metaClazzList) {
			generateCodeByMetaClazz(metaClazz);
		}
	}
	
	public static void main(String[] args) {
		String fileName = "CodeModel.xml";
		CodeGenerator codeGenerator = new CodeGenerator();
		codeGenerator.initCodeModelFromClassPath(fileName);
		codeGenerator.initConfiguration();
		codeGenerator.generateCodeAllClazz();
		System.out.println("生成结束");
	}
}

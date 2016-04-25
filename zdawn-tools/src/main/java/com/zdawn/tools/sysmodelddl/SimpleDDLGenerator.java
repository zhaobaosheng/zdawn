package com.zdawn.tools.sysmodelddl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.ModelFactory;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.commons.sysmodel.metaservice.SysModel;

public class SimpleDDLGenerator {
	//db field type properties
	private Properties fieldTypeProperties = null;
	
	private static List<String> NUMBERS = null;
	
	private static String STRING = "java.lang.String";
	
	public static String MYSQL = "mysql";
	
	public static String SQLSERVER = "sqlserver";
	
	public static String ORACLE = "oracle";
	
	static{
		NUMBERS = new ArrayList<String>();
		NUMBERS.add("java.lang.Boolean");
		NUMBERS.add("java.lang.Short");
		NUMBERS.add("java.lang.Integer");
		NUMBERS.add("java.lang.Long");
		NUMBERS.add("java.lang.Double");
		NUMBERS.add("java.lang.Float");
		NUMBERS.add("java.math.BigDecimal");
	}
	
	private void loadFieldTypeProperty(String dbType) throws Exception{
		InputStream in = null;
		try {
			fieldTypeProperties = new Properties();
			in = this.getClass().getClassLoader().getResourceAsStream(dbType+".properties");
			fieldTypeProperties.load(in);
		} catch (IOException e) {
			throw new Exception("load "+dbType+".properties file error "+e.getMessage());
		}finally{
			try {
				if(in!=null) in.close();
			} catch (Exception e) {}
		}
	}
	/**
	 * 建表语句
	 */
	public String genCreateTable(Entity entity,String dbType){
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(entity.getTableName());
		sb.append('\n');
		sb.append('(');
		List<Property> list = entity.getProperties();
		boolean first = true;
		for (Property property : list) {
			if(!property.isUsing()) continue;
			if(first){
				first = false;
			}else{
				sb.append(',');
			}
			sb.append('\n');
			sb.append(genOneField(property, dbType));
		}
		//primary key
		if(entity.getUniqueColumn()!=null && !entity.getUniqueColumn().equals("")){
			sb.append(',');
			sb.append('\n');
			sb.append("primary key (").append(entity.getUniqueColumn()).append(')');
		}
		sb.append('\n');
		sb.append(");");
		return sb.toString();
	}
	
	private String genOneField(Property property,String dbType){
		StringBuilder sb = new StringBuilder();
		String nativeType = fieldTypeProperties.getProperty(property.getType());
		nativeType = nativeType==null ? property.getType():nativeType.trim();
		if(STRING.equals(property.getType())){//string
			sb.append(property.getColumn());
			sb.append(' ').append(nativeType).append('(').append(property.getLength()).append(')');
			if(property.isNotNull()) sb.append(" not null");
			if(property.getDefaultValue()!=null && !property.getDefaultValue().equals("")){
				sb.append(" default '").append(property.getDefaultValue()).append('\'');
			}
		}else if(NUMBERS.contains(property.getType())){//number
			sb.append(property.getColumn());
			sb.append(' ').append(nativeType);
			if(property.getLength()>0) {
				sb.append('(').append(property.getLength());
				if(property.getScale()>0) sb.append(',').append(property.getScale());
				sb.append(')');
			}
			if(property.isNotNull()) sb.append(" not null");
			if(property.getDefaultValue()!=null && !property.getDefaultValue().equals("")){
				if(MYSQL.equals(dbType) && property.getDefaultValue().equals("auto_increment"))
					sb.append(' ').append(property.getDefaultValue());
				else if(SQLSERVER.equals(dbType) && property.getDefaultValue().equals("auto_increment"))
					sb.append(" identity(1,1) ").append(property.getDefaultValue());
				else sb.append(" default ").append(property.getDefaultValue());
			}
		}else{//others
			sb.append(property.getColumn());
			sb.append(' ').append(nativeType);
			if(property.isNotNull()) sb.append(" not null");
		}
		return sb.toString();
	}
	/**
	 * 删除表语句
	 */
	public String genDropTable(Entity entity){
		return "drop table "+entity.getTableName()+";";
	}
	/**
	 * 删除主键语句
	 */
	public String genDropPrimaryKey(Entity entity){
		return "alter table "+entity.getTableName()+" drop primary key;";
	}
	/**
	 * 添加主键语句
	 */
	public String genAddPrimaryKey(Entity entity){
		return "alter table "+entity.getTableName()+" add primary key("+entity.getUniqueColumn()+");";
	}
	/**
	 * 新增字段
	 */
	public String genAddOneField(String tableName,Property property,String dbType){
		return "alter table "+tableName+" add column "+genOneField(property, dbType)+';';
	}
	/**
	 * 删除字段
	 */
	public String genDropOneField(String tableName,Property property){
		return "alter table "+tableName+" drop column "+property.getColumn()+';';
	}
	/**
	 * 更新字段
	 */
	public String genModifyOneField(String tableName,Property property,String dbType){
		return "alter table "+tableName+" modify column "+genOneField(property, dbType)+';';
	}
	/**
	 * 字段注释
	 */
	public String genFieldComments(Entity entity,String dbType){
		StringBuilder sb = new StringBuilder();
		List<Property> listProperty = entity.getProperties();
		if(MYSQL.equals(dbType)){
			for (Property property : listProperty) {
				sb.append("alter table "+entity.getTableName()+" modify column "+genOneField(property, dbType));
				sb.append(" comment ").append('\'').append(property.getDescription()).append("';");
				sb.append('\n');
			}
		}else if(SQLSERVER.equals(dbType)){
			System.out.println("comment not realize");
		}else if(ORACLE.equals(dbType)){
			for (Property property : listProperty) {
				sb.append("comment on table ").append(entity.getTableName());
				sb.append('.').append(property.getColumn()).append(" is '").append(property.getDescription()).append("';");
				sb.append('\n');
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			SimpleDDLGenerator  generator = new SimpleDDLGenerator();
			generator.loadFieldTypeProperty(SimpleDDLGenerator.MYSQL);
			ModelFactory.loadQueryConfigFromFileSystem("E:/mygit/zdawn/zdawn-commons/src/main/resources/DataModel.xml");
			SysModel sysModel = ModelFactory.getSysModel();
			Collection<Entity>  list = sysModel.getEntities().values();
			for (Entity entity : list) {
				String temp = generator.genCreateTable(entity, SimpleDDLGenerator.MYSQL);
				System.out.println(temp);
				temp = generator.genDropTable(entity);
				System.out.println(temp);
				temp = generator.genAddPrimaryKey(entity);
				System.out.println(temp);
				temp = generator.genDropPrimaryKey(entity);
				System.out.println(temp);
				List<Property> listProperty = entity.getProperties();
				for (Property property : listProperty) {
					temp = generator.genAddOneField(entity.getTableName(), property, SimpleDDLGenerator.MYSQL);
					System.out.println(temp);
					temp = generator.genModifyOneField(entity.getTableName(), property, SimpleDDLGenerator.MYSQL);
					System.out.println(temp);
					temp = generator.genDropOneField(entity.getTableName(), property);
					System.out.println(temp);
				}
				temp = generator.genFieldComments(entity,SimpleDDLGenerator.MYSQL);
				System.out.println(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

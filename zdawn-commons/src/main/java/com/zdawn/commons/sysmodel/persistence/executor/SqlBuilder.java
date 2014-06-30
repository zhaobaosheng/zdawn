package com.zdawn.commons.sysmodel.persistence.executor;

import java.util.ArrayList;
import java.util.List;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;

public class SqlBuilder {

	public static String createInsertSql(String tableName,List<Property> propertyList) {
		StringBuilder sb = new StringBuilder("insert into "+tableName+'(');
		StringBuilder sbHolder = new StringBuilder("values(");
		for (int i = 0; i < propertyList.size(); i++) {
			Property property = propertyList.get(i);
			if(i==0){
				sb.append(property.getColumn());
				sbHolder.append('?');
			}else{
				sb.append(',').append(property.getColumn());
				sbHolder.append(",?");
			}
		}
		sb.append(')');
		sbHolder.append(')');
		sb.append(' ').append(sbHolder.toString());
		return sb.toString();
	}

	public static List<Property> filterProperty(List<Property> propertyList,
			String ...excludeProperty) {
		if(excludeProperty==null) return propertyList;
		if(excludeProperty.length==1){
			Property unique = null;
			for (Property property : propertyList) {
				if(property.getName().equalsIgnoreCase(excludeProperty[0])){
					unique = property;
					break;
				}
			}
			propertyList.remove(unique);
			return propertyList;
		}
		List<String> filterKey = new ArrayList<String>();
		for (int i = 0; i < excludeProperty.length; i++) {
			filterKey.add(excludeProperty[i].toUpperCase());
		}
		List<Property> temp = new ArrayList<Property>();
		for (Property property : propertyList) {
			if(filterKey.contains(property.getColumn().toUpperCase())) continue;
			temp.add(property);
		}
		return temp;
	}

	public static String createUpdateSql(String tableName, Property unique,
			List<Property> propertyList) {
		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(tableName).append(" set ");
		for (int i = 0; i < propertyList.size(); i++) {
			Property property = propertyList.get(i);
			if(i==0){
				sb.append(property.getColumn()).append("=?");
			}else{
				sb.append(',').append(property.getColumn()).append("=?");
			}
		}
		sb.append(" where ").append(unique.getColumn()).append("=?");
		return sb.toString();
	}

	public static String createSelectEntitySql(Entity entity,Property property,Object id, boolean number) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * ").append("from ").append(entity.getTableName());
		sb.append(" where ").append(property.getColumn()).append("=");
		if(number){
			sb.append(id);
		}else{
			sb.append('\'').append(id).append('\'');
		}
		return sb.toString();
	}

	public static String createDeleteSql(String tableName, String column,Object id, boolean number) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(tableName);
		sb.append(" where ").append(column).append('=');
		if(number){
			sb.append(id);
		}else{
			sb.append('\'').append(id).append('\'');
		}
		return sb.toString();
	}

}

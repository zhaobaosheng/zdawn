package com.zdawn.commons.sysmodel.persistence.support;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.support.TypeUtil;
import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.ModelFactory;
import com.zdawn.commons.sysmodel.metaservice.Property;
import com.zdawn.util.Utils;
import com.zdawn.util.beans.BeanUtil;
import com.zdawn.util.convert.ConvertUtil;

/**
 * 数据转换类
 * <br>根据数据字典进行数据转换
 * @author zhaobs
 */
public class DataConverter {
	
	private static final Logger log = LoggerFactory.getLogger(DataConverter.class);
	/**
	 * 字符串格式数据转换成其对应对象
	 * @param entityName 实体名
	 * @param data 数据
	 * @param prefix 数据key前缀
	 * @return 转换后对象集合  null 没有数据转换
	 */
	public static Map<String,Object> convertToObjects(String entityName,Map<String,String> data,String prefix){
		Entity entity = ModelFactory.getSysModel().findEntityByName(entityName);
		Map<String,Object> result = new HashMap<String, Object>();
		if(prefix==null){
			for (Property property : entity.getProperties()) {
				if(!property.isUsing()) continue;
				String value = data.get(property.getName());
				if(value==null) continue;
				Object obj = ConvertUtil.convertToObject(property.getType(), value,property.getToStringformat());
				if(obj==null){
					log.warn(property.getDescription()+"("+property.getName()+")转换失败!");
					continue;
				}
				result.put(property.getName(),obj);
			}
		}else{
			for (Property property : entity.getProperties()) {
				if(!property.isUsing()) continue;
				String value = data.get(prefix+property.getName());
				if(value==null) continue;
				Object obj = ConvertUtil.convertToObject(property.getType(), value,property.getToStringformat());
				if(obj==null){
					log.warn(property.getDescription()+"("+property.getName()+")转换失败!");
					continue;
				}
				result.put(property.getName(),obj);
			}
		}
		return result.size()==0 ? null:result;
	}
	/**
	 * 字符串格式数据转换成其对应对象
	 * @param entityName 实体名
	 * @param data 数据
	 * @return 转换后对象集合  null 没有数据转换
	 */
	public static Map<String,Object> convertToObjects(String entityName,Map<String,String> data){
		return convertToObjects(entityName, data,null);
	}
	/**
	 * 字符串格式数据转换JavaBean
	 * @param entityName 实体名
	 * @param clazz 类名
	 * @param data 数据
	 * @param prefix 数据key前缀
	 * @return JavaBean对象
	 */
	public static <T> T convertToBean(String entityName,Class<T> clazz,Map<String,String> data,String prefix){
		Map<String,String> propertyData = data;
		if(prefix != null){
			propertyData = Utils.truncateKeyPrefix(prefix, data, false);
		}
		Map<String,String> propertyNameFormat = new HashMap<String, String>();
		Entity entity = ModelFactory.getSysModel().findEntityByName(entityName);
		for (Property property : entity.getProperties()) {
			if(property.isUsing() && TypeUtil.isDate(property.getType())){
				propertyNameFormat.put(property.getName(),property.getToStringformat());
			}
		}
		return BeanUtil.bindBean(clazz, propertyData,null, propertyNameFormat);
	}
	/**
	 * 字符串格式数据转换JavaBean
	 * @param entityName 实体名
	 * @param clazz 类名
	 * @param data 数据
	 * @return JavaBean对象
	 */
	public static <T> T convertToBean(String entityName,Class<T> clazz,Map<String,String> data){
		return convertToBean(entityName, clazz, data,null);
	}
}

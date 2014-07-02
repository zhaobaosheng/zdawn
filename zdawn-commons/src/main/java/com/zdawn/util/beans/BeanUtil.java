package com.zdawn.util.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.sqlquery.model.QueryConfig;
import com.zdawn.util.convert.ConvertUtil;

public class BeanUtil {
	private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);
	private static final Set<String> needFormatClazz = new HashSet<String>(5);
	private static final Map<String,String> defaultClazzFormat = new HashMap<String,String>(4);
	static{
		needFormatClazz.add("java.util.Date");
		needFormatClazz.add("java.sql.Date");
		needFormatClazz.add("java.sql.Timestamp");
		defaultClazzFormat.put("java.util.Date","yyyy-MM-dd");
		defaultClazzFormat.put("java.sql.Date","yyyy-MM-dd");
		defaultClazzFormat.put("java.sql.Timestamp","yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 获取Bean指定属性值
	 * @param bean 对象
	 * @param descriptor 属性描述
	 * @return object
	 */
	public static Object getBeanOnePropertyValue(Object bean,PropertyDescriptor descriptor){
		try {
			Method method = descriptor.getReadMethod();
			return method.invoke(bean);
		} catch (Exception e) {
			log.error("getBeanOnePropertyValue", e);
		}
		return null;
	}
	/**
	 * JavaBean对象转换成属性Map集合
	 * <br>获取全部属性Map集合
	 * @param value JavaBean对象
	 * @param excludePropertyName制定不需要转换属性名
	 * @return 属性名为keyMap集合
	 */
	public static Map<String,Object> transformBeanToMap(Object value,String... excludePropertyName){
		if(value==null) return null;
		Map<String,Object> propertyData = new HashMap<String, Object>();
		try {
			PropertyDescriptor[] propertyDescriptors = getAllBeanPropertyDescriptor(value.getClass());
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				 Method method = propertyDescriptor.getReadMethod();
				 if(exist(excludePropertyName, propertyDescriptor.getName())) continue;
				 propertyData.put(propertyDescriptor.getName(),method.invoke(value));
			}
			
		} catch (Exception e) {
			log.error("transformBeanToMap", e);
		}
		return propertyData;
	}
	/**
	 * JavaBean对象转换成属性Map集合
	 * @param value JavaBean对象
	 * @param propertyNames 转换属性数组
	 * @return 属性名为keyMap集合
	 */
	public static Map<String,Object> transformBeanPropertyToMap(Object value,String[] propertyNames){
		if(value==null || propertyNames==null) return null;
		Map<String,Object> propertyData = new HashMap<String, Object>();
		try {
			PropertyDescriptor[] propertyDescriptors = getAllBeanPropertyDescriptor(value.getClass());
			for (int i = 0; i < propertyNames.length; i++) {
				PropertyDescriptor propertyDes = getPropertyDescriptor(propertyNames[i],propertyDescriptors);
			    if(propertyDes==null) continue;
			    Method method = propertyDes.getReadMethod();
				propertyData.put(propertyDes.getName(),method.invoke(value));
			}
		} catch (Exception e) {
			log.error("transformBeanPropertyToMap", e);
		}
		return propertyData;
	}
	/**
	 * 字符串格式数据绑定JavaBean对象
	 * @param clazz JavaBean类
	 * @param propertyData 字符串格式数据
	 * @param classFormat 属性类型转换格式 可以为空
	 * @param propertyNameFormat 具体属性转换格式 可以为空
	 * @return JavaBean对象
	 */
	public static <T> T bindBean(Class<T> clazz,Map<String,String> propertyData,
			Map<Class<?>,String> classFormat,Map<String,String> propertyNameFormat) {
		T obj = null;
		try {
			obj = clazz.newInstance();
			PropertyDescriptor[] propertyDescriptors = getAllBeanPropertyDescriptor(clazz);
			for(Map.Entry<String,String> entry:propertyData.entrySet()){
				PropertyDescriptor propertyDes = getPropertyDescriptor(entry.getKey(),propertyDescriptors);
			    if(propertyDes==null) continue;
			    Method method = propertyDes.getWriteMethod();
			    //字符串转换成对应属性对象
			    String propertyClazz = propertyDes.getPropertyType().getName();
			    String format = findPropertyFormat(propertyDes.getPropertyType(),propertyDes.getName(),
			    		classFormat,propertyNameFormat);
			    Object pvalue = ConvertUtil.convertToObject(propertyClazz,entry.getValue(), format);
			    method.invoke(obj,pvalue);
			} 
		} catch (Exception e) {
			log.error("bindBean", e);
		}
		return obj;
	}
	private static String findPropertyFormat(Class<?> propertyClass,String propertyName,
			Map<Class<?>, String> classFormat,Map<String, String> propertyNameFormat) {
		if(needFormatClazz.contains(propertyClass.getName())){
			if(propertyNameFormat!=null){
				String format = propertyNameFormat.get(propertyName);
				if(format!=null) return format;
			}
			if(classFormat!=null){
				String format = classFormat.get(propertyClass);
				if(format!=null) return format;
			}
			return defaultClazzFormat.get(propertyClass.getName());
		}
		return null;
	}
	/**
	 * 属性名为keyMap集合绑定到指定JavaBean
	 * @param clazz JavaBean类
	 * @param propertyData 属性Map集合
	 * @return JavaBean对象
	 */
	public static <T> T bindBean(Class<T> clazz,Map<String,Object> propertyData){
		T obj = null;
		try {
			obj = clazz.newInstance();
			PropertyDescriptor[] propertyDescriptors = getAllBeanPropertyDescriptor(clazz);
			for(Map.Entry<String,Object> entry:propertyData.entrySet()){
				PropertyDescriptor propertyDes = getPropertyDescriptor(entry.getKey(),propertyDescriptors);
			    if(propertyDes==null) continue;
			    Method method = propertyDes.getWriteMethod();
			    method.invoke(obj, entry.getValue());
			} 
		} catch (Exception e) {
			log.error("bindBean", e);
		}
		return obj;
	}
	/**
	 * 类对象绑定属性值
	 * @param clazz 类全名
	 * @param propertyData 属性数据
	 * @return 类对象实例
	 */
	public static Object bindBean(String clazz,Map<String,Object> propertyData){
		Object obj = null;
		try {
			Class<?> objClass = loadClass(clazz);
			obj = objClass.newInstance();
			PropertyDescriptor[] propertyDescriptors = getAllBeanPropertyDescriptor(objClass);
			for(Map.Entry<String,Object> entry:propertyData.entrySet()){
				PropertyDescriptor propertyDes = getPropertyDescriptor(entry.getKey(),propertyDescriptors);
			    if(propertyDes==null) continue;
			    Method method = propertyDes.getWriteMethod();
			    method.invoke(obj, entry.getValue());
			} 
		} catch (Exception e) {
			log.error("bindBean", e);
		}
		return obj;
	}
	private static PropertyDescriptor getPropertyDescriptor(
			String propertyName, PropertyDescriptor[] propertyDescriptors) {
		if(propertyDescriptors==null) return null;
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyName.equals(propertyDescriptor.getName()))
				return propertyDescriptor;
		}
		return null;
	}
	//获取JavaBean属性描述
	private static PropertyDescriptor[] getAllBeanPropertyDescriptor(Class<?> clazz){
		PropertyDescriptor[] propertyDescriptors = null;
		try {
			propertyDescriptors = BeanPropertyDescriptorCache.getBeanPropertyDescriptor(clazz);
			if(propertyDescriptors==null){
				BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Introspector.USE_ALL_BEANINFO);
				propertyDescriptors = beanInfo.getPropertyDescriptors();
				//不使用内置缓存
				Introspector.flushFromCaches(clazz);
				BeanPropertyDescriptorCache.addBeanPropertyDescriptor(clazz,propertyDescriptors);
			}
		} catch (IntrospectionException e) {
			log.error("getBeanPropertyDescriptor", e);
		}
		return propertyDescriptors;
	}
	/**
	 * 获取类属性描述
	 * <br>如果属性名在类中不存在其对应属性描述为null
	 * @param clazz 类
	 * @param propertyName 属性名集合
	 * @return 属性描述数组
	 */
	public static PropertyDescriptor[] getBeanPropertyDescriptors(Class<?> clazz,String ...propertyName){
		PropertyDescriptor[] all = getAllBeanPropertyDescriptor(clazz);
		if(propertyName==null || propertyName.length==0) return all;
		PropertyDescriptor[] array = new PropertyDescriptor[propertyName.length];
		for (int i = 0; i < propertyName.length; i++) {
			array[i] = getPropertyDescriptor(propertyName[i],all);
		}
		return array;
	}
	public static PropertyDescriptor getBeanPropertyDescriptor(Class<?> clazz,String propertyName){
		return getBeanPropertyDescriptors(clazz,propertyName)[0];
	}
	/**
	 * 获取Bean属性信息
	 * @param clazz Class
	 * @return key属性名 value属性类型
	 */
	public static Map<String,Class<?>> getBeanPropertyInfo(Class<?> clazz){
		PropertyDescriptor[] all = getAllBeanPropertyDescriptor(clazz);
		if(all==null) return null;
		Map<String,Class<?>> propertyInfo = new HashMap<String, Class<?>>();
		for (int i = 0; i < all.length; i++) {
			propertyInfo.put(all[i].getName(), all[i].getPropertyType());
		}
		return propertyInfo;
	}
	
	public static boolean exist(String[] array,String value){
		if(array==null || array.length==0) return false;
		for (int i = 0; i < array.length; i++) {
			if(value.equals(array[1])) return true;
		}
		return false;
	}
	/**
	 * 为Bean对象绑定属性
	 * @param bean 对象实例
	 * @param propertyNames 属性数组
	 * @param values 属性对应值
	 */
	public static void bindBeanProperties(Object bean,String[] propertyNames,Object[] values){
		if(bean==null) return ;
		try {
			PropertyDescriptor[] propertyDescriptors = getAllBeanPropertyDescriptor(bean.getClass());
			for (int i = 0; i < propertyNames.length; i++) {
				PropertyDescriptor propertyDes = getPropertyDescriptor(propertyNames[i],propertyDescriptors);
			    if(propertyDes==null) continue;
			    Method method = propertyDes.getWriteMethod();
			    method.invoke(bean,values[i]);
			}
		}catch (Exception e) {
			log.error("bindBeanProperties", e);
		}
	}
	public static void bindBeanOneProperty(Object bean,String propertyName,Object value){
		bindBeanProperties(bean, new String[]{propertyName}, new Object[]{value});
	}
	public static Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return BeanUtil.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }
	public static void main(String[] arg){
		Map<String,String> propertyMap = new HashMap<String, String>();
		propertyMap.put("code","111111");
		propertyMap.put("paging","true");
		QueryConfig config = BeanUtil.bindBean(QueryConfig.class,propertyMap,null,null);
		System.out.println(config.getCode());
	}
}

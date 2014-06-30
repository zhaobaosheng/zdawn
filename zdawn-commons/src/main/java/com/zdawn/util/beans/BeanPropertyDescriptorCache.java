package com.zdawn.util.beans;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BeanPropertyDescriptorCache {
	private static int cacheCount = 500;
	private static Map<Class<?>, PropertyDescriptor[]> propertyDescriptorCache = Collections
			.synchronizedMap(new HashMap<Class<?>, PropertyDescriptor[]>());
	private static Map<Class<?>, Long> keyTimeMap = Collections
			.synchronizedMap(new HashMap<Class<?>, Long>());
	
	public static void setCacheCount(int count){
		cacheCount = count;
	}
	
	public static PropertyDescriptor[] getBeanPropertyDescriptor(Class<?> clazz){
		PropertyDescriptor[] tmp = propertyDescriptorCache.get(clazz);
		if(tmp!=null) keyTimeMap.put(clazz, System.currentTimeMillis());
		return tmp;
	}
	
	public static void addBeanPropertyDescriptor(Class<?> clazz, PropertyDescriptor[] tmp){
		if(propertyDescriptorCache.size()>cacheCount){
			Long min = System.currentTimeMillis()+1;
			Class<?> minClazz = null;
			for (Map.Entry<Class<?>, Long> entry : keyTimeMap.entrySet()) {
				if(entry.getValue()<min){
					 min = entry.getValue();
					 minClazz = entry.getKey();
				}
			}
			propertyDescriptorCache.remove(minClazz);
			keyTimeMap.remove(minClazz);
		}
		propertyDescriptorCache.put(clazz, tmp);
		keyTimeMap.put(clazz,System.currentTimeMillis());
	}
}

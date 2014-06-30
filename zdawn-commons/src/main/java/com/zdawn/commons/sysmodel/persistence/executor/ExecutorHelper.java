package com.zdawn.commons.sysmodel.persistence.executor;

import java.util.ArrayList;
import java.util.List;

import com.zdawn.commons.sysmodel.metaservice.Entity;
import com.zdawn.commons.sysmodel.metaservice.Property;

public class ExecutorHelper {
	/**
	 * 为属性排序，按照实体属性集合中的顺序排序。
	 * @param entity 实体
	 * @param propertyList 属性列表
	 * @return 如果排序失败返回传入属性集合
	 */
	public static List<Property> sortEntityProperty(Entity entity,List<Property> propertyList){
		List<Property> sortList = new ArrayList<Property>();
		for (Property temp : entity.getProperties()) {
			for (Property property : propertyList) {
				if(temp==property){
					sortList.add(temp);
					break;
				}
			}
		}
		return sortList.size()==propertyList.size() ? sortList:propertyList;
	}
}

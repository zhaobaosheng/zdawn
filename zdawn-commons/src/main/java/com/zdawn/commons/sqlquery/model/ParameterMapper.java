package com.zdawn.commons.sqlquery.model;

import java.util.ArrayList;
import java.util.List;
/**
 * 查询参数项集合
 * @author zhaobs
 *
 */
public class ParameterMapper {
	private List<ParameterItem> parameterItemList = null;

	public List<ParameterItem> getParameterItemList() {
		return parameterItemList;
	}

	public void setParameterItemList(List<ParameterItem> parameterItemList) {
		this.parameterItemList = parameterItemList;
	}
	
	public void addParameterItem(ParameterItem item){
		if(parameterItemList==null) parameterItemList = new ArrayList<ParameterItem>();
		parameterItemList.add(item);
	}
	
	public ParameterItem getParameterItem(String property){
		if(parameterItemList==null) return null;
		for (ParameterItem item : parameterItemList) {
			if(item.getProperty().equals(property)) return item;
		}
		return null;
	}
	
	public List<ParameterItem> getNonEmptyParameterItem(){
		if(parameterItemList==null) return null;
		ArrayList<ParameterItem> nonEmptyList = new ArrayList<ParameterItem>();
		for (ParameterItem item : parameterItemList) {
			if(!item.isEmpty()) nonEmptyList.add(item);
		}
		return nonEmptyList;
	}
}

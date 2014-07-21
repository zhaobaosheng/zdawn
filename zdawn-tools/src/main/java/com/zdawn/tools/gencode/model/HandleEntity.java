package com.zdawn.tools.gencode.model;

import java.util.ArrayList;
import java.util.List;

public class HandleEntity {
	
	private String name;
	
	private String tableName;
	
	private boolean main;
	/**
	 *	index 0 propertyName
	 *  index 1 column
	 */
	private List<String[]> dataItems;
	
	public boolean isMain() {
		return main;
	}
	public void setMain(boolean main) {
		this.main = main;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<String[]> getDataItems() {
		return dataItems;
	}
	public void addDataItem(String[] item){
		if(dataItems==null) dataItems = new ArrayList<String[]>();
		dataItems.add(item);
	}
	public void setDataItems(List<String[]> dataItems) {
		this.dataItems = dataItems;
	}
}

package com.zdawn.commons.sqlquery.model;

import java.util.ArrayList;
import java.util.List;
/**
 * 查询结果项描述集合
 * @author zhaobs
 *
 */
public class ResultMapper {
	//标识
	private String id;
	//stringArray or objectArray or stringMap or objectMap
	private String type;
	//查询结果项描述
	private List<ResultItem> resultItemList = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<ResultItem> getResultItemList() {
		return resultItemList;
	}

	public void setResultItemList(List<ResultItem> resultItemList) {
		this.resultItemList = resultItemList;
	}
	
	public void addResultItem(ResultItem item){
		if(resultItemList==null) resultItemList = new ArrayList<ResultItem>();
		resultItemList.add(item);
	}
}

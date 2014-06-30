package com.zdawn.commons.sqlquery.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询配置信息
 * @author zhaobs
 */
public class MetaQuery {
	//版本
	private String version;
	//查询结果映射项
	private Map<String,ResultMapper> resultMappers = new HashMap<String,ResultMapper>();
	//查询集合
	private Map<String,QueryConfig> queryConfigs = new HashMap<String,QueryConfig>();
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Map<String, ResultMapper> getResultMappers() {
		return resultMappers;
	}
	public Map<String, QueryConfig> getQueryConfigs() {
		return queryConfigs;
	}
	public void addResultMapper(ResultMapper resultMapper){
		resultMappers.put(resultMapper.getId(), resultMapper);
	}
	public ResultMapper getResultMapper(String id){
		return resultMappers.get(id);
	}
	public void addQueryConfig(QueryConfig queryConfig){
		queryConfigs.put(queryConfig.getCode(),queryConfig);
	}
	public QueryConfig getQueryConfig(String code){
		return queryConfigs.get(code);
	}
}

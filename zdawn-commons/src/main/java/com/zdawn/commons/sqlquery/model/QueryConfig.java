package com.zdawn.commons.sqlquery.model;

/**
 * 单个查询配置描述
 * @author zhaobs
 *
 */
public class QueryConfig {
	//唯一标识
	private String code;
	//中文名称
	private String name;
	//是否分页
	private boolean paging;
	//引用resultMap的id属性
	private String resultMapper;
	//查询参数项描述
	private ParameterMapper parameterMapper;
	/**
	 * 查询sql内容
	 * ${xx}替换参数
	 * #{xx}替换成占位符?
	 * $[] 替换内容块
	 */
	private String selectSql;
	//分页使用--计算查询记录数
	private String countSql;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPaging() {
		return paging;
	}
	public void setPaging(boolean paging) {
		this.paging = paging;
	}
	public String getResultMapper() {
		return resultMapper;
	}
	public void setResultMapper(String resultMapper) {
		this.resultMapper = resultMapper;
	}
	public ParameterMapper getParameterMapper() {
		return parameterMapper;
	}
	public void setParameterMapper(ParameterMapper parameterMapper) {
		this.parameterMapper = parameterMapper;
	}
	public String getSelectSql() {
		return selectSql;
	}
	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}
	public String getCountSql() {
		return countSql;
	}
	public void setCountSql(String countSql) {
		this.countSql = countSql;
	}
}

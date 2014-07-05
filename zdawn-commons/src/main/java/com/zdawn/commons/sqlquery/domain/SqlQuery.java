package com.zdawn.commons.sqlquery.domain;

import java.util.List;
import java.util.Map;

import com.zdawn.commons.jdbc.PageDataSet;


public interface SqlQuery {
	/**
	 * 查询List&lt;Map&lt;String, String&gt;&gt;格式数据
	 */
	public List<Map<String, String>> queryStringMapData(String code,Map<String, Object> para) throws Exception;
	/**
	 * 分页查询PageDataSet&lt;Map&lt;String,String&gt;&gt;格式数据
	 */
	public PageDataSet<Map<String,String>> queryPagingStringMapData(String code,Map<String, Object> para) throws Exception;
	/**
	 * 查询List&lt;Map&lt;String,Object&gt;&gt;格式数据
	 */
	public List<Map<String,Object>> queryObjectMapData(String code,Map<String, Object> para) throws Exception ;
	/**
	 * 查询指定类型数据
	 */
	public <T> List<T> queryObjectMapData(String code,Map<String, Object> para,Class<T> clazz) throws Exception;
	/**
	 * 分页查询PageDataSet&lt;Map&lt;String,Object&gt;&gt;格式数据
	 */
	public PageDataSet<Map<String,Object>> queryPagingObjectMapData(String code,Map<String, Object> para) throws Exception;
	/**
	 * 分页查询指定类型数据
	 */
	public <T> PageDataSet<T> queryPagingObjectMapData(String code,Map<String, Object> para,Class<T> clazz) throws Exception;
	/**
	 * 查询List&lt;String[]&gt;格式数据
	 */
	public List<String[]> queryStringArrayData(String code,Map<String, Object> para) throws Exception;
	/**
	 * 查询List&lt;Object[]&gt;格式数据
	 */
	public List<Object[]> queryObjectArrayData(String code,Map<String, Object> para) throws Exception;
}

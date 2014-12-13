package com.zdawn.text.lucene.index;

import java.util.Map;
/**
 * 索引器
 * @author zhaobs
 */
public interface Indexer {
	/**
	 * 创建索引
	 * @param dbName 索引库名字
	 * @param docName 文档名字
	 * @param fieldData 字段值 key为字段名字
	 */
	public void createIndex(String dbName,String docName,Map<String,Object> fieldData) throws Exception;
	/**
	 * 创建或更新索引
	 @param dbName 索引库名字
	 * @param docName 文档名字
	 * @param fieldData 字段值 key为字段名字
	 */
	public void updateIndex(String dbName,String docName,Map<String,Object> fieldData) throws Exception;
	/**
	 * 删除索引
	 * @param dbName 索引库名字
	 * @param docName 文档名字
	 * @param id 文档唯一值
	 */
	public void deleteIndex(String dbName,String docName,String id) throws Exception;
	/**
	 * 提交索引变化并持久化
	 * <br>此方法必须在创建、更新、删除索引完成后调用
	 * @param dbName 索引库名字
	 * @throws Exception
	 */
	public void commitChange(String dbName) throws Exception;
}

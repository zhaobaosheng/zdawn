package com.zdawn.commons.sysmodel.persistence;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

public interface SqlSession {
	/**
	 * 保存实体
	 * @param entityName 实体名称
	 * @param data 实体数据
	 * @return 实体唯一标识
	 * @throws PersistenceException
	 */
	public Serializable save(String entityName,Map<String,Object> data) throws PersistenceException;
	
	public <T> Serializable save(String entityName,T object) throws PersistenceException;
	/**
	 * 更新实体
	 * @param entityName 实体名称
	 * @param data 实体数据
	 * @throws PersistenceException
	 */
	public void update(String entityName,Map<String,Object> data) throws PersistenceException;
	
	public <T> void update(String entityName,T object) throws PersistenceException;
	/**
	 * 删除实体
	 * @param entityName 实体名称
	 * @param id 唯一标识
	 * @throws PersistenceException
	 */
	public void delete(String entityName,Object id) throws PersistenceException;
	/**
	 * 获取对象
	 * <br>对象不存在返回 null
	 * @param entityName 实体名称
	 * @param id 唯一标识
	 * @return 实体数据
	 * @throws PersistenceException
	 */
	public Map<String,Object> getData(String entityName,Object id) throws PersistenceException;
	
	public <T> T get(Class<T> clazz,String entityName,Object id) throws PersistenceException;
	/**
	 * 获取当前会话数据库连接
	 */
	public Connection getConnection();
	/**
	 * 自动提交
	 * <br>会话每个方法为一个事物
	 */
	public void setAutoCommit(boolean commit);
	/**
	 * 是否自动提交
	 */
	public boolean isAutoCommit();
	/**
	 * 提交事物
	 */
	public void commit();
	/**
	 * 回滚事物
	 */
	public void rollback();
	/**
	 * 关闭会话
	 * <br>不会强制提交或回滚事物
	 */
	public void close();
}

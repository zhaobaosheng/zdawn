package com.zdawn.commons.sysmodel.persistence.executor;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;

/**
 * 存储数据接口
 * @author zhaobs
 * 2014-06-10
 */
public interface Executor {
	/**
	 * 新增操作
	 * @param entityName 实体名
	 * @param data 数据
	 * <br> oneToOne实体的key为首字母小写的实体名称（数据字典中实体的 entityName属性） 
	 * <br> oneToMany实体的key为首字母小写的实体名称（数据字典中实体的 entityName属性）+List
	 * @param sysModel 数据字典
	 * @param con 数据库连接
	 * @return 实体ID
	 * @throws PersistenceException
	 */
	public Serializable insert(String entityName, Map<String, Object> data,
			SysModel sysModel, Connection con) throws PersistenceException;
	/**
	 * 新增操作
	 * @param entityName 实体名
	 * @param object 实体对象
	 * <br> oneToOne实体对象的属性为首字母小写的实体名称（数据字典中实体的 entityName属性） 
	 * <br> oneToMany实体对象的属性为首字母小写的实体名称（数据字典中实体的 entityName属性）+List
	 * @param sysModel 数据字典
	 * @param con 数据库连接
	 * @return 实体ID
	 * @throws PersistenceException
	 */
	public <T> Serializable insert(String entityName, T object,
			SysModel sysModel, Connection con) throws PersistenceException;
	/**
	 * 更新操作
	 * @param entityName 实体名
	 * @param data 数据
	 * <br> oneToOne实体的key为首字母小写的实体名称（数据字典中实体的 entityName属性） 
	 * <br> oneToMany实体的key为首字母小写的实体名称（数据字典中实体的 entityName属性）+List
	 * @param sysModel 数据字典
	 * @param con 数据连接
	 * @throws PersistenceException
	 */
	public void update(String entityName, Map<String, Object> data,
			SysModel sysModel, Connection con) throws PersistenceException;
	/**
	 * 更新操作
	 * @param entityName 实体名
	 * @param object 实体对象
	 * <br> oneToOne实体对象的属性为首字母小写的实体名称（数据字典中实体的 entityName属性） 
	 * <br> oneToMany实体对象的属性为首字母小写的实体名称（数据字典中实体的 entityName属性）+List
	 * @param sysModel 数据字典
	 * @param con 数据连接
	 * @throws PersistenceException
	 */
	public <T> void update(String entityName, T object, SysModel sysModel,
			Connection con) throws PersistenceException;
	/**
	 * 删除操作
	 * <br>同时删除与之关联的实体
	 * @param entityName 实体名
	 * @param id 对象主键
	 * @param sysModel 数据字典
	 * @param con 数据连接
	 * @throws PersistenceException
	 */
	public void delete(String entityName, Object id, SysModel sysModel,
			Connection con) throws PersistenceException;
	/**
	 * 键值对方式获取对象
	 * <br> oneToOne实体的key为首字母小写的实体名称（数据字典中实体的 entityName属性） 
	 * <br> oneToMany实体的key为首字母小写的实体名称（数据字典中实体的 entityName属性）+List
	 * @param entityName 实体名
	 * @param propertyName 属性名 null代表主键
	 * @param id 对象主键或是唯一值
	 * @param excludeChildEntity true为不包括子实体对象
	 * @param sysModel 数据字典
	 * @param con 数据连接
	 * @param loadChilds 加载子实体列表
	 * @return Map &lt;String, Object&gt;
	 * @throws PersistenceException
	 */
	public Map<String, Object> getData(String entityName, String propertyName,
			Object id, boolean excludeChildEntity,SysModel sysModel,
			Connection con,String... loadChilds) throws PersistenceException;
	/**
	 * 获取实体对象
	 * @param clazz 实体Java类
	 * @param entityName 实体名
	 * @param propertyName 属性名 null代表主键
	 * @param id 对象主键或是唯一值
	 * @param excludeChildEntity true为不包括子实体对象
	 * @param sysModel 数据字典
	 * @param con 数据连接
	 * @param loadChilds 加载子实体列表
	 * @return T 实体对象
	 * @throws PersistenceException
	 */
	public <T> T get(Class<T> clazz, String entityName, String propertyName,
			Object id, boolean excludeChildEntity, SysModel sysModel,
			Connection con,String... loadChilds) throws PersistenceException;
	
	public void batchInsertData(String entityName,List<Map<String, Object>> data,
			SysModel sysModel, Connection con) throws PersistenceException;
	
	public <T> void batchInsertClazz(String entityName, List<T> data,
			SysModel sysModel, Connection con) throws PersistenceException;
	
	public void batchUpdateData(String entityName,List<Map<String, Object>> data,
			SysModel sysModel, Connection con) throws PersistenceException;
	
	public <T> void batchUpdateClazz(String entityName, List<T> data,
			SysModel sysModel, Connection con) throws PersistenceException;
}

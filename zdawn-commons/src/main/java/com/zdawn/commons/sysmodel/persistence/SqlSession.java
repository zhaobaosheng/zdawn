package com.zdawn.commons.sysmodel.persistence;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
	 * 获取实体对象,包括子实体对象。
	 * <br>对象不存在返回 null
	 * @param entityName 实体名称
	 * @param id 主键
	 * @return 实体对象
	 * @throws PersistenceException
	 */
	public Map<String,Object> getData(String entityName,Object id) throws PersistenceException;
	/**
	 * 获取实体对象
	 * <br>对象不存在返回 null
	 * @param entityName 实体名称
	 * @param id 主键
	 * @return 实体对象
	 * @param excludeChildEntity true为不包括子实体对象
	 * @throws PersistenceException
	 */
	public Map<String,Object> getData(String entityName,Object id,boolean excludeChildEntity) throws PersistenceException;
	/**
	 * 获取实体对象，包括子实体对象。
	 * <br>对象不存在返回 null
	 * @param clazz 实体类-Entity元素clazz属性对应类
	 * @param entityName 实体名
	 * @param id 主键
	 * @return 实体对象
	 * @throws PersistenceException
	 */
	public <T> T get(Class<T> clazz,String entityName,Object id) throws PersistenceException;
	/**
	 * 获取实体对象
	 * <br>对象不存在返回 null
	 * @param clazz 实体类-Entity元素clazz属性对应类
	 * @param entityName 实体名
	 * @param id 主键
	 * @param excludeChildEntity  true为不包括子实体对象
	 * @return 实体对象
	 * @throws PersistenceException
	 */
	public <T> T get(Class<T> clazz,String entityName,Object id,boolean excludeChildEntity) throws PersistenceException;
	/**
	 * 获取实体对象,包括引用子实体对象。
	 * <br>对象不存在返回 null
	 * @param entityName 实体名称
	 * @param propertyName 唯一值对应属性名
	 * @param uniqueValue 唯一值
	 * @return 实体对象
	 * @throws PersistenceException
	 */
	public Map<String,Object> getData(String entityName,String propertyName,Object uniqueValue) throws PersistenceException;
	/**
	 * 获取实体对象
	 * <br>对象不存在返回 null
	 * @param entityName 实体名称
	 * @param propertyName 唯一值对应属性名
	 * @param uniqueValue 唯一值
	 * @param excludeChildEntity true为不包括子实体对象
	 * @return 实体对象
	 * @throws PersistenceException
	 */
	public Map<String,Object> getData(String entityName,String propertyName,Object uniqueValue,boolean excludeChildEntity) throws PersistenceException;
	/**
	 * 获取实体对象，包括子实体对象。
	 * <br>对象不存在返回 null
	 * @param clazz Entity元素clazz属性对应类
	 * @param entityName 实体名称
	 * @param propertyName  唯一值对应属性名
	 * @param uniqueValue 唯一值
	 * @return  实体对象
	 * @throws PersistenceException
	 */
	public <T> T get(Class<T> clazz,String entityName,String propertyName,Object uniqueValue) throws PersistenceException;
	/**
	 * 获取实体对象
	 * <br>对象不存在返回 null
	 * @param clazz Entity元素clazz属性对应类
	 * @param entityName 实体名称
	 * @param propertyName 唯一值对应属性名
	 * @param uniqueValue 唯一值
	 * @param excludeChildEntity true为不包括子实体对象
	 * @return 实体对象
	 * @throws PersistenceException
	 */
	public <T> T get(Class<T> clazz,String entityName,String propertyName,Object uniqueValue,boolean excludeChildEntity) throws PersistenceException;
	/**
	 * 执行sql语句
	 * @param sql 语句
	 * @param para 输入对象参数
	 * @return 影响行数
	 * @throws SQLException
	 */
	public int executeSql (String sql,Object ... para) throws PersistenceException;
	/**
	 * 执行一组sql语句
	 * @param sqls SQL语句集合
	 * @return int[] 数组-数组每个元素代表sql执行影响行数
	 * @throws PersistenceException
	 */
	public int[] executeArraySql(List<String> sqls) throws PersistenceException;
	public void batchSaveData (String entityName,List<Map<String,Object>> data) throws PersistenceException;
	public void batchUpdateData (String entityName,List<Map<String,Object>> data) throws PersistenceException;
	public <T> void batchSaveClazz (String entityName, List<T> data) throws PersistenceException;
	public <T> void batchUpdateClazz (String entityName, List<T> data) throws PersistenceException;
	/**
	 * 查询单条字符串格式数据
	 * @param sql  SQL语句
	 * @param para 查询输入条件
	 * @return String[] 字符串数组
	 * @throws PersistenceException
	 */
	public String[] searchOneRow(String sql,Object... para) throws PersistenceException;
	/**
	 * 查询字符串格式数据
	 * @param sql  SQL语句
	 * @param para 查询输入条件
	 * @return List&lt;String[]&gt;
	 * @throws PersistenceException
	 */
	public List<String[]> getSearchResult(String sql,Object... para) throws PersistenceException;
	/**
	 * 查询对象数组格式数据
	 * @param sql SQL语句
	 * @param objectTypes 查询语句列表对应数据类型，顺序保持一致。
	 * @param para 查询输入条件
	 * @return List&lt;Object[]&gt;
	 * @throws PersistenceException
	 */
	public List<Object[]> getSearchResult(String sql,String[] objectTypes,Object... para) throws PersistenceException;
	/**
	 * 查询Map格式数据
	 * @param sql SQL语句
	 * @param objectTypes 查询列表对应数据类型，顺序保持一致。
	 * @param keyAlias 查询列表对应别名，顺序保持一致。如果null从ResultSetMetaData获取
	 * @param para 查询输入条件
	 * @return List&lt;Map&lt;String,Object&gt;&gt;
	 * @throws PersistenceException
	 */
	public List<Map<String,Object>> getSearchResult(String sql,String[] objectTypes,String[] keyAlias,Object... para) throws PersistenceException;
	/**
	 * 查询集合对象，默认情况按类的属性名和类型获取数据。
	 * @param clazz 对象类
	 * @param sql  SQL语句
	 * @param specialType 配置属性名和类型可覆盖类中属性，可以为null。
	 * @param para 查询输入条件
	 * @return List&lt;T&gt;
	 * @throws PersistenceException
	 */
	public <T> List<T> getSearchResult(Class<T> clazz,String sql,Map<String,String> specialType,Object... para) throws PersistenceException;
	/**
	 * 查询统计函数结果
	 * @param sql SQL语句
	 * @param para  查询输入条件
	 * @return long
	 * @throws PersistenceException
	 */
	public long getFunctionNumber (String sql,Object ... para) throws PersistenceException;
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

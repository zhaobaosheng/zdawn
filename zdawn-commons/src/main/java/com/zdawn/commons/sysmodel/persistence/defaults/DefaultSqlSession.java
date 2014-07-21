package com.zdawn.commons.sysmodel.persistence.defaults;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.JdbcUtils;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.commons.sysmodel.persistence.PersistenceException;
import com.zdawn.commons.sysmodel.persistence.SqlSession;
import com.zdawn.commons.sysmodel.persistence.executor.Executor;

public class DefaultSqlSession implements SqlSession {
	private static final Logger log = LoggerFactory.getLogger(DefaultSqlSession.class);
	
	public DefaultSqlSession(Executor executor,SysModel sysModel,
			Connection connection,boolean antoCommit){
		this.executor = executor;
		this.sysModel = sysModel;
		this.connection = connection;
		setAutoCommit(antoCommit);
	}
	/**
	 * this need manual set connection auto commit
	 */
	public DefaultSqlSession(Executor executor,SysModel sysModel,
			Connection connection){
		this.executor = executor;
		this.sysModel = sysModel;
		this.connection = connection;
	}
	/**
	 * 执行存储接口
	 */
	private Executor executor = null;
	/**
	 * 数据字典
	 */
	private SysModel sysModel = null;
	/**
	 * 数据库连接
	 */
	private Connection connection = null;
	/**
	 * 每个方法是否自动提交事物
	 */
	private boolean antoCommit = false;
	@Override
	public Serializable save(String entityName, Map<String, Object> data)
			throws PersistenceException {
		return executor.insert(entityName, data, sysModel, connection);
	}

	@Override
	public <T> Serializable save(String entityName, T object)
			throws PersistenceException {
		return executor.insert(entityName, object, sysModel, connection);
	}

	@Override
	public void update(String entityName, Map<String, Object> data)
			throws PersistenceException {
		executor.update(entityName, data, sysModel, connection);

	}

	@Override
	public <T> void update(String entityName, T object)
			throws PersistenceException {
		executor.update(entityName, object, sysModel, connection);
	}

	@Override
	public void delete(String entityName, Object id)
			throws PersistenceException {
		executor.delete(entityName, id, sysModel, connection);
	}

	@Override
	public Map<String, Object> getData(String entityName, Object id)
			throws PersistenceException {
		return executor.getData(entityName,null, id,false, sysModel, connection);
	}
	
	@Override
	public Map<String, Object> getData(String entityName, Object id,
			boolean excludeChildEntity) throws PersistenceException {
		return executor.getData(entityName, null, id, excludeChildEntity, sysModel, connection);
	}
	@Override
	public Map<String, Object> getData(String entityName, String propertyName,
			Object uniqueValue) throws PersistenceException {
		return executor.getData(entityName, propertyName, uniqueValue, false, sysModel, connection);
	}
	@Override
	public Map<String, Object> getData(String entityName, String propertyName,
			Object uniqueValue, boolean excludeChildEntity)
			throws PersistenceException {
		return executor.getData(entityName, propertyName, uniqueValue, excludeChildEntity, sysModel, connection);
	}
	@Override
	public Map<String, Object> getData(String entityName, Object id,
			String... loadChildEntity) throws PersistenceException {
		return executor.getData(entityName,null, id,false, sysModel, connection,loadChildEntity);
	}
	@Override
	public <T> T get(Class<T> clazz,String entityName, Object id) throws PersistenceException {
		return executor.get(clazz, entityName,null, id,false, sysModel, connection);
	}
	
	@Override
	public <T> T get(Class<T> clazz, String entityName, Object id,
			boolean excludeChildEntity) throws PersistenceException {
		return executor.get(clazz, entityName, null, id, excludeChildEntity, sysModel, connection);
	}
	@Override
	public <T> T get(Class<T> clazz, String entityName, String propertyName,
			Object uniqueValue) throws PersistenceException {
		return executor.get(clazz, entityName, propertyName, uniqueValue, false, sysModel, connection);
	}
	@Override
	public <T> T get(Class<T> clazz, String entityName, String propertyName,
			Object uniqueValue, boolean excludeChildEntity)
			throws PersistenceException {
		return executor.get(clazz, entityName, propertyName, uniqueValue, excludeChildEntity, sysModel, connection);
	}
	@Override
	public <T> T get(Class<T> clazz, String entityName, Object id,
			String... loadChildEntity) throws PersistenceException {
		return executor.get(clazz, entityName,null, id,false, sysModel, connection,loadChildEntity);
	}
	@Override
	public void batchSaveData(String entityName, List<Map<String, Object>> data)
			throws PersistenceException {
		executor.batchInsertData(entityName, data, sysModel, connection);
	}
	@Override
	public void batchUpdateData(String entityName,
			List<Map<String, Object>> data) throws PersistenceException {
		executor.batchUpdateData(entityName, data, sysModel, connection);
	}
	@Override
	public <T> void batchSaveClazz(String entityName, List<T> data)
			throws PersistenceException {
		executor.batchInsertClazz(entityName, data, sysModel, connection);
	}
	@Override
	public <T> void batchUpdateClazz(String entityName, List<T> data)
			throws PersistenceException {
		executor.batchUpdateClazz(entityName, data, sysModel, connection);
	}
	@Override
	public String[] searchOneRow(String sql, Object... para)
			throws PersistenceException {
		String[] data = null;
		try {
			data = JdbcUtils.searchOneRow(connection, sql, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return data;
	}
	
	@Override
	public List<String[]> getSearchResult(String sql, Object... para)
			throws PersistenceException {
		ArrayList<String[]> data = null;
		try {
			data = JdbcUtils.getSearchResult(connection, sql, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return data;
	}
	
	@Override
	public long getFunctionNumber(String sql, Object... para)
			throws PersistenceException {
		long result = 0L;
		try {
			result = JdbcUtils.getFunctionNumber(connection, sql, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return result;
	}
	
	@Override
	public List<Object[]> getSearchResult(String sql, String[] objectTypes,
			Object... para) throws PersistenceException {
		List<Object[]> data = null;
		try {
			data = JdbcUtils.getSearchResult(connection, sql, objectTypes, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return data;
	}
	@Override
	public List<Map<String, Object>> getSearchResult(String sql,
			String[] objectTypes, String[] keyAlias, Object... para)
			throws PersistenceException {
		List<Map<String, Object>> data = null;
		try {
			data = JdbcUtils.getSearchResult(connection, sql, objectTypes, keyAlias, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return data;
	}
	@Override
	public <T> List<T> getSearchResult(Class<T> clazz, String sql,
			Map<String, String> specialType, Object... para)
			throws PersistenceException {
		 List<T> data = null;
		try {
			data = JdbcUtils.getSearchResult(connection, clazz, sql, specialType, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return data;
	}
	
	@Override
	public int executeSql(String sql, Object... para)
			throws PersistenceException {
		int result = 0;
		try {
			result = JdbcUtils.executeSql(connection, sql, para);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return result;
	}
	@Override
	public int[] executeArraySql(List<String> sqls) throws PersistenceException {
		int[] result = null;
		try {
			result = JdbcUtils.executeArraySql(connection, sqls);
		} catch (SQLException e) {
			throw new PersistenceException("",e.getMessage());
		}
		return result;
	}
	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void setAutoCommit(boolean commit) {
		try {
			if(connection!=null){
				connection.setAutoCommit(commit);
				antoCommit = commit;
			}
		} catch (SQLException e) {
			log.error("setAutoCommit", e);
		}
	}

	@Override
	public boolean isAutoCommit() {
		return antoCommit;
	}

	@Override
	public void commit() {
		if(antoCommit) throw new UnsupportedOperationException("every operation auto commit transaction");
		try {
			if(connection!=null){
				connection.commit();
			}
		} catch (SQLException e) {
			log.error("commit", e);
		}
	}

	@Override
	public void rollback() {
		if(antoCommit) throw new UnsupportedOperationException("every operation auto commit transaction");
		try {
			if(connection!=null){
				connection.rollback();
			}
		} catch (SQLException e) {
			log.error("rollback", e);
		}
	}

	@Override
	public void close() {
		try {
			if(connection!=null){
				if(antoCommit) connection.setAutoCommit(true);
				connection.close();
			}
			sysModel = null;
			executor = null;
		} catch (SQLException e) {
			log.error("rollback", e);
		}
	}
}

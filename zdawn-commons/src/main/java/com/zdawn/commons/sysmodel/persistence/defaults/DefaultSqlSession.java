package com.zdawn.commons.sysmodel.persistence.defaults;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		return executor.getData(entityName, id, sysModel, connection);
	}

	@Override
	public <T> T get(Class<T> clazz,String entityName, Object id) throws PersistenceException {
		return executor.get(clazz, entityName, id, sysModel, connection);
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

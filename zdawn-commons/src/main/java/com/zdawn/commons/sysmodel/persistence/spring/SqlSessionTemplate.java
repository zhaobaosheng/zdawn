package com.zdawn.commons.sysmodel.persistence.spring;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.sysmodel.persistence.PersistenceException;
import com.zdawn.commons.sysmodel.persistence.SqlSession;
import com.zdawn.commons.sysmodel.persistence.SqlSessionFactory;

public class SqlSessionTemplate implements SqlSession {
	private static final Logger log = LoggerFactory.getLogger(SqlSessionTemplate.class);
	
	private SqlSessionFactory sqlSessionFactory;
	
	private SqlSession sqlSessionProxy;
	
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
		this.sqlSessionFactory = sqlSessionFactory;
		this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
		        SqlSessionFactory.class.getClassLoader(),
		        new Class[] { SqlSession.class },
		        new SqlSessionInterceptor());
	}
	
	@Override
	public Serializable save(String entityName, Map<String, Object> data)
			throws PersistenceException {
		return sqlSessionProxy.save(entityName, data);
	}

	@Override
	public <T> Serializable save(String entityName, T object)
			throws PersistenceException {
		return sqlSessionProxy.save(entityName, object);
	}

	@Override
	public void update(String entityName, Map<String, Object> data)
			throws PersistenceException {
		sqlSessionProxy.update(entityName, data);
	}

	@Override
	public <T> void update(String entityName, T object)
			throws PersistenceException {
		sqlSessionProxy.update(entityName, object);
	}

	@Override
	public void delete(String entityName, Object id)
			throws PersistenceException {
		sqlSessionProxy.delete(entityName, id);
	}

	@Override
	public Map<String, Object> getData(String entityName, Object id)
			throws PersistenceException {
		return sqlSessionProxy.getData(entityName, id);
	}

	@Override
	public <T> T get(Class<T> clazz, String entityName, Object id)
			throws PersistenceException {
		return sqlSessionProxy.get(clazz, entityName, id);
	}

	@Override
	public Connection getConnection() {
		return sqlSessionProxy.getConnection();
	}

	@Override
	public void setAutoCommit(boolean commit) {
		throw new UnsupportedOperationException("Manual set auto commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public boolean isAutoCommit() {
		throw new UnsupportedOperationException("Manual get auto commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void commit() {
		throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void rollback() {
		throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
	}
	private class SqlSessionInterceptor implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			SqlSession sqlSession = SqlSessionUtils.getSqlSession(sqlSessionFactory);
			try {
				Object result = method.invoke(sqlSession, args);
				return result;
			} catch (Exception e) {
				log.error("invoke", e);
				throw SqlSessionUtils.unwrapThrowable(e);
			}
		}
	}
}

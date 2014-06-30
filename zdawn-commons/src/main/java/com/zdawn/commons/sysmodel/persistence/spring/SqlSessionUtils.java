package com.zdawn.commons.sysmodel.persistence.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.zdawn.commons.sysmodel.persistence.SqlSession;
import com.zdawn.commons.sysmodel.persistence.SqlSessionFactory;

public final class SqlSessionUtils {
	private static final Logger log = LoggerFactory.getLogger(SqlSessionUtils.class);
	
	public static SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
		SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		if(holder != null && holder.isSynchronizedWithTransaction()) {
			if (log.isDebugEnabled()) {
				log.debug("Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction");
			}
			return holder.getSqlSession();
		}
		if (log.isDebugEnabled()) {
			log.debug("Creating a new SqlSession");
		}
		Connection con = DataSourceUtils.getConnection(sessionFactory.getDataSource());
		SqlSession session = sessionFactory.openSession(con);
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			holder = new SqlSessionHolder(session);
			TransactionSynchronizationManager.bindResource(sessionFactory, holder);
	        TransactionSynchronizationManager.registerSynchronization(new SqlSessionSynchronization(holder, sessionFactory));
	        holder.setSynchronizedWithTransaction(true);
		}else{
			if(log.isDebugEnabled()) {
				log.debug("SqlSession [" + session + "] was not registered for synchronization because synchronization is not active");
			}
		}
		return session;
	}
	public static Throwable unwrapThrowable(Throwable wrapped) {
	    Throwable unwrapped = wrapped;
	    while (true) {
	      if (unwrapped instanceof InvocationTargetException) {
	        unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
	      } else if (unwrapped instanceof UndeclaredThrowableException) {
	        unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
	      } else {
	        return unwrapped;
	      }
	    }
	}
}
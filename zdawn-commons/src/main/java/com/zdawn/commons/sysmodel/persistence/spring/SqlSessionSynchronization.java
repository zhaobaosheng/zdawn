package com.zdawn.commons.sysmodel.persistence.spring;

import static org.springframework.util.Assert.notNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.zdawn.commons.sysmodel.persistence.SqlSessionFactory;

public class SqlSessionSynchronization extends TransactionSynchronizationAdapter{
	private static final Logger log = LoggerFactory.getLogger(SqlSessionSynchronization.class);
	
    private final SqlSessionHolder holder;

    private final SqlSessionFactory sessionFactory;

    private boolean holderActive = true;

    public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory) {
    	notNull(holder, "Parameter 'holder' must be not null");
        notNull(sessionFactory, "Parameter 'sessionFactory' must be not null");
    	this.holder = holder;
    	this.sessionFactory = sessionFactory;
    }

    @Override
    public int getOrder() {
      return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 1;
    }

    @Override
    public void suspend() {
      if (this.holderActive) {
        if (log.isDebugEnabled()) {
          log.debug("Transaction synchronization suspending SqlSession [" + this.holder.getSqlSession() + "]");
        }
        TransactionSynchronizationManager.unbindResource(this.sessionFactory);
      }
    }

    @Override
    public void resume() {
      if (this.holderActive) {
        if (log.isDebugEnabled()) {
          log.debug("Transaction synchronization resuming SqlSession [" + this.holder.getSqlSession() + "]");
        }
        TransactionSynchronizationManager.bindResource(this.sessionFactory, this.holder);
      }
    }
    
    @Override
	public void beforeCompletion() {
    	if(this.holderActive) {
	   		 if (log.isDebugEnabled()) {
	   	          log.debug("Transaction  before Completion , Transaction synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]");
	   	     }
	   		this.holderActive = false;
	   		TransactionSynchronizationManager.unbindResource(sessionFactory);
    	}
	}
    
    @Override
    public void afterCompletion(int status) {
    	 if(this.holderActive){
    		 if (log.isDebugEnabled()) {
    	          log.debug("holder is active , Transaction  synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]");
    	      }
    		 TransactionSynchronizationManager.unbindResourceIfPossible(sessionFactory);
    	     this.holderActive = false;
    	 }
    	 this.holder.reset();
    }
}
package com.zdawn.commons.sysmodel.persistence.spring;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import com.zdawn.commons.sysmodel.metaservice.ModelFactory;
import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.commons.sysmodel.persistence.SqlSessionFactory;
import com.zdawn.commons.sysmodel.persistence.defaults.DefaultSqlSessionFactory;
import com.zdawn.commons.sysmodel.persistence.executor.Executor;

public class SqlSessionFactoryBean implements FactoryBean <SqlSessionFactory>,InitializingBean {
	
	private SqlSessionFactory sqlSessionFactory = null;
	
	private Executor executor = null;
	
	private SysModel sysModel = null;
	
	private DataSource dataSource = null;
	
	private String regexDataModelFileName = "DataModel\\w*\\.xml";
	
	@Override
	public SqlSessionFactory getObject() throws Exception {
		return sqlSessionFactory;
	}

	@Override
	public Class<?> getObjectType() {
		return SqlSessionFactory.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ModelFactory.loadQueryConfigFromClassPathByRegexFileName(regexDataModelFileName);
		sysModel = ModelFactory.getSysModel();
		sqlSessionFactory = new DefaultSqlSessionFactory(executor, sysModel, dataSource);
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setDataSource(DataSource dataSource) {
		if (dataSource instanceof TransactionAwareDataSourceProxy) {
	      // If we got a TransactionAwareDataSourceProxy, we need to perform
	      // transactions for its underlying target DataSource, else data
	      // access code won't see properly exposed transactions (i.e.
	      // transactions for the target DataSource).
	      this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
	    } else {
	      this.dataSource = dataSource;
	    }
	}
}

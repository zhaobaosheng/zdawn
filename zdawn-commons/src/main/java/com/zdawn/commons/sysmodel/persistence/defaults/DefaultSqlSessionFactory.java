package com.zdawn.commons.sysmodel.persistence.defaults;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zdawn.commons.sysmodel.metaservice.SysModel;
import com.zdawn.commons.sysmodel.persistence.SqlSession;
import com.zdawn.commons.sysmodel.persistence.SqlSessionFactory;
import com.zdawn.commons.sysmodel.persistence.executor.Executor;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
	
	private Executor executor = null;
	
	private SysModel sysModel = null;
	
	private DataSource dataSource = null;
	
	public DefaultSqlSessionFactory(Executor executor,SysModel sysModel,DataSource dataSource){
		this.executor = executor;
		this.sysModel = sysModel;
		this.dataSource = dataSource;
	}
	
	@Override
	public SqlSession openSession(boolean operateAutoCommit) {
		DefaultSqlSession defaultSqlSession = null;
		try {
			defaultSqlSession = new DefaultSqlSession(executor, sysModel, dataSource.getConnection(),operateAutoCommit);
		} catch (SQLException e) {
			throw new RuntimeException("gain Connection failture from DataSource");
		}
		return defaultSqlSession;
	}

	@Override
	public SqlSession openSession(Connection con,boolean operateAutoCommit) {
		return new DefaultSqlSession(executor, sysModel,con,operateAutoCommit);
	}
	
	@Override
	public SqlSession openSession(Connection con) {
		return new DefaultSqlSession(executor, sysModel,con);
	}

	@Override
	public SysModel getDataDictionary() {
		return sysModel;
	}
	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}

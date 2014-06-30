package com.zdawn.commons.sysmodel.persistence;

import java.sql.Connection;

import javax.sql.DataSource;

import com.zdawn.commons.sysmodel.metaservice.SysModel;

public interface SqlSessionFactory {
	/**
	 * 创建会话
	 * <br>operateAutoCommit 创建每次操作自动提交事物的会话
	 */
	public SqlSession openSession(boolean operateAutoCommit);
	/**
	 * 根据给定数据库连接创建会话
	 * <br>operateAutoCommit 创建每次操作自动提交事物的会话
	 */
	public SqlSession openSession(Connection con,boolean operateAutoCommit);
	/**
	 * 根据给定数据库连接创建会话
	 */
	public SqlSession openSession(Connection con);
	/**
	 * 获取数据字典信息 
	 */
	public SysModel getDataDictionary();
	/**
	 * 获取数据源
	 */
	public DataSource getDataSource();
}

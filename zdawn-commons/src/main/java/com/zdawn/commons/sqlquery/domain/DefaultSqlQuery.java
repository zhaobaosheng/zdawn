package com.zdawn.commons.sqlquery.domain;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.zdawn.commons.jdbc.JdbcUtils;
import com.zdawn.commons.jdbc.PageDataSet;
import com.zdawn.commons.jdbc.PagingSqlDecorator;

public class DefaultSqlQuery implements SqlQuery {
	/**
	 * 数据源
	 */
	private DataSource dataSource = null;
	
	private QueryExecutor queryExecutor = null;
	
	public DefaultSqlQuery(){
	}
	public DefaultSqlQuery(String queryRegxFileName,PagingSqlDecorator pagingSqlDecorator,DataSource dataSource){
		queryExecutor = new QueryExecutor(queryRegxFileName,pagingSqlDecorator);
		if(dataSource !=null) this.dataSource = dataSource;
	}
	
	public DefaultSqlQuery(String queryRegxFileName,PagingSqlDecorator pagingSqlDecorator){
		this(queryRegxFileName,pagingSqlDecorator,null);
	}
	
	public DefaultSqlQuery(PagingSqlDecorator pagingSqlDecorator,DataSource dataSource){
		this(null,pagingSqlDecorator,dataSource);
	}
	
	public DefaultSqlQuery(PagingSqlDecorator pagingSqlDecorator){
		this(null,pagingSqlDecorator,null);
	}
	@Override
	public List<Map<String, String>> queryStringMapData(String code,
			Map<String, Object> para) throws Exception {
		Connection connection = null;
		List<Map<String, String>> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryStringMapData(connection, code, para);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public PageDataSet<Map<String, String>> queryPagingStringMapData(
			String code, Map<String, Object> para) throws Exception {
		Connection connection = null;
		PageDataSet<Map<String, String>> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryPagingStringMapData(connection, code, para);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public List<Map<String, Object>> queryObjectMapData(String code,
			Map<String, Object> para) throws Exception {
		Connection connection = null;
		List<Map<String, Object>> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryObjectMapData(connection, code, para);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public <T> List<T> queryObjectMapData(String code,
			Map<String, Object> para, Class<T> clazz) throws Exception {
		Connection connection = null;
		List<T> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryObjectMapData(connection, code, para,clazz);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public PageDataSet<Map<String, Object>> queryPagingObjectMapData(
			String code, Map<String, Object> para) throws Exception {
		Connection connection = null;
		PageDataSet<Map<String, Object>> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryPagingObjectMapData(connection, code, para);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public <T> PageDataSet<T> queryPagingObjectMapData(String code,
			Map<String, Object> para, Class<T> clazz) throws Exception {
		Connection connection = null;
		PageDataSet<T> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryPagingObjectMapData(connection, code, para,clazz);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public List<String[]> queryStringArrayData(String code,
			Map<String, Object> para) throws Exception {
		Connection connection = null;
		List<String[]> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryStringArrayData(connection, code, para);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	@Override
	public List<Object[]> queryObjectArrayData(String code,
			Map<String, Object> para) throws Exception {
		Connection connection = null;
		List<Object[]> data = null;
		try {
			connection = dataSource.getConnection();
			data = queryExecutor.queryObjectArrayData(connection, code, para);
		}finally{
			JdbcUtils.closeConnection(connection);
		}
		return data;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setQueryExecutor(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}
	
}

package com.zdawn.commons.sqlquery.domain;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.zdawn.commons.jdbc.PageDataSet;

/**
 * SqlQuery工厂-根据输入参数（数据库连接）创建通用查询接口
 * @author zhaobs
 * 2014-07-05
 */
public class SqlQueryFactory {
	private QueryExecutor queryExecutor = null;

	public SqlQuery  createSqlQuery(Connection connection){
		return new SqlQueryImpl(queryExecutor,connection);
	}
	public void setQueryExecutor(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}
	class SqlQueryImpl implements SqlQuery {
		
		private QueryExecutor queryExecutor;
		
		private Connection connection;
		
		public SqlQueryImpl(QueryExecutor queryExecutor,Connection connection){
			this.queryExecutor = queryExecutor;
			this.connection = connection;
		}
		@Override
		public List<Map<String, String>> queryStringMapData(String code,
				Map<String, Object> para) throws Exception {
			return queryExecutor.queryStringMapData(connection, code, para);
		}

		@Override
		public PageDataSet<Map<String, String>> queryPagingStringMapData(
				String code, Map<String, Object> para) throws Exception {
			return queryExecutor.queryPagingStringMapData(connection, code, para);
		}

		@Override
		public List<Map<String, Object>> queryObjectMapData(String code,
				Map<String, Object> para) throws Exception {
			return queryExecutor.queryObjectMapData(connection, code, para);
		}

		@Override
		public <T> List<T> queryObjectMapData(String code,
				Map<String, Object> para, Class<T> clazz) throws Exception {
			return queryExecutor.queryObjectMapData(connection, code, para,clazz);
		}

		@Override
		public PageDataSet<Map<String, Object>> queryPagingObjectMapData(
				String code, Map<String, Object> para) throws Exception {
			return queryExecutor.queryPagingObjectMapData(connection, code, para);
		}

		@Override
		public <T> PageDataSet<T> queryPagingObjectMapData(String code,
				Map<String, Object> para, Class<T> clazz) throws Exception {
			return queryExecutor.queryPagingObjectMapData(connection, code, para,clazz);
		}

		@Override
		public List<String[]> queryStringArrayData(String code,
				Map<String, Object> para) throws Exception {
			return queryExecutor.queryStringArrayData(connection, code, para);
		}

		@Override
		public List<Object[]> queryObjectArrayData(String code,
				Map<String, Object> para) throws Exception {
			return queryExecutor.queryObjectArrayData(connection, code, para);
		}
	}
}

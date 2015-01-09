package com.zdawn.commons.sqlquery.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.JdbcUtils;
import com.zdawn.commons.jdbc.PageDataSet;
import com.zdawn.commons.jdbc.PagingSqlDecorator;
import com.zdawn.commons.jdbc.support.AbstractType;
import com.zdawn.commons.jdbc.support.TypeUtil;
import com.zdawn.commons.sqlquery.model.MetaQuery;
import com.zdawn.commons.sqlquery.model.MetaQueryFactory;
import com.zdawn.commons.sqlquery.model.ParameterItem;
import com.zdawn.commons.sqlquery.model.ParameterMapper;
import com.zdawn.commons.sqlquery.model.QueryConfig;
import com.zdawn.commons.sqlquery.model.ResultItem;
import com.zdawn.commons.sqlquery.model.ResultMapper;
import com.zdawn.util.beans.BeanUtil;
import com.zdawn.util.convert.ConvertUtil;

/**
 * sql-query 查询实现
 * @author zhaobs
 * 2014-05-15
 */
public class QueryExecutor {
	private final Logger log = LoggerFactory.getLogger(QueryExecutor.class);
	/**
	 * sql符号缓存
	 */
	private Map<String,List<Token>> sqlTokenMap = new HashMap<String, List<Token>>();
	/**
	 * 查询配置文件名称
	 * 在classpath路径下装在多个配置文件
	 */
	private String queryRegxFileName = "Sql-Query\\w*\\.xml";
	/**
	 * 查询配置对象
	 */
	private MetaQuery metaQuery = null;
	/**
	 * 分页装饰器
	 */
	private PagingSqlDecorator pagingSqlDecorator = null;
	
	/**
	 * 缺省构造函数-装载查询配置
	 */
	public QueryExecutor(String queryRegxFileName,PagingSqlDecorator pagingSqlDecorator){
		if(queryRegxFileName !=null) this.queryRegxFileName = queryRegxFileName;
		this.pagingSqlDecorator= pagingSqlDecorator;
		MetaQueryFactory.loadQueryConfigFromClassPathByRegexFileName(this.queryRegxFileName );
		metaQuery = MetaQueryFactory.getMetaQuery();
	}
	public QueryExecutor(PagingSqlDecorator pagingSqlDecorator){
		this(null,pagingSqlDecorator);
	}

	public List<Map<String, String>> queryStringMapData(Connection connection,String code,
			Map<String, Object> para) throws Exception {
		QueryConfig queryConfig = gainAndValidateQueryConfig(code, para);
		if(queryConfig.isPaging()) throw new Exception("should invoke paging method");
		List<Token> selectSqlToken = getSelectSqlToken(queryConfig);
		if(selectSqlToken==null) throw new Exception("query sql is null");
		ResultMapper resultMapper = metaQuery.getResultMapper(queryConfig.getResultMapper());
		if(resultMapper==null) throw new Exception("not found ResultMapper for Query Config");
		//create sql
		String sql = transformSql(selectSqlToken,queryConfig.getParameterMapper(),para);
		List<QuestionFixToken> tokenList = getQuestionFixTokenList(selectSqlToken,para);
		return searchStringMapData(connection,sql, tokenList, queryConfig.getParameterMapper(), resultMapper, para);
	}
	public PageDataSet<Map<String,String>> queryPagingStringMapData(Connection connection,String code,
			Map<String, Object> para) throws Exception{
		QueryConfig queryConfig = gainAndValidateQueryConfig(code, para);
		if(!queryConfig.isPaging()) throw new Exception("should invoke nonpaging method");
		int currentPage = 1;
		Object temp = para.get("page");
		if(temp!=null){
			if(temp instanceof String) currentPage = Integer.parseInt(temp.toString());
			else currentPage = (Integer)temp;
		}
		int limit = 10;
		temp = para.get("rows");
		if(temp!=null){
			if(temp instanceof String) limit = Integer.parseInt(temp.toString());
			else limit = (Integer)temp;
		}
		List<Token> selectSqlToken = getSelectSqlToken(queryConfig);
		if(selectSqlToken==null) throw new Exception("query sql is null");
		ResultMapper resultMapper = metaQuery.getResultMapper(queryConfig.getResultMapper());
		if(resultMapper==null) throw new Exception("not found ResultMapper for Query Config");
		//create sql
		String sql = transformSql(selectSqlToken,queryConfig.getParameterMapper(),para);
		List<QuestionFixToken> questionTokenList = getQuestionFixTokenList(selectSqlToken,para);
		List<Token> countSqlToken = getCountSqlToken(queryConfig);
		int total = 0;
		if(countSqlToken==null){
			String countSql = "select count(*) from ("+sql+") T";
			total = getTotalCount(connection,countSql,questionTokenList,queryConfig.getParameterMapper(),para);
		}else{
			String countSql = transformSql(countSqlToken,queryConfig.getParameterMapper(),para);
			List<QuestionFixToken> countTokenList = getQuestionFixTokenList(countSqlToken,para);
			total = getTotalCount(connection,countSql,countTokenList,queryConfig.getParameterMapper(),para);
		}
		String pageSql = pagingSqlDecorator.decoratePagingSql(sql,limit,total,currentPage);
		if(pageSql.equals("")) pageSql=sql;
		List<Map<String, String>> data = searchStringMapData(connection,pageSql,questionTokenList,queryConfig.getParameterMapper(),resultMapper,para);
		PageDataSet<Map<String, String>> dataSet = new PageDataSet<Map<String,String>>(data);
		dataSet.setTotal(total);
		return dataSet;
	}
	public List<Map<String,Object>> queryObjectMapData(Connection connection,String code,
			Map<String, Object> para) throws Exception {
		QueryConfig queryConfig = gainAndValidateQueryConfig(code, para);
		if(queryConfig.isPaging()) throw new Exception("should invoke paging method");
		List<Token> selectSqlToken = getSelectSqlToken(queryConfig);
		if(selectSqlToken==null) throw new Exception("query sql is null");
		ResultMapper resultMapper = metaQuery.getResultMapper(queryConfig.getResultMapper());
		if(resultMapper==null) throw new Exception("not found ResultMapper for Query Config");
		List<ResultItem> resultItemList = resultMapper.getResultItemList();
		if(resultItemList==null || resultItemList.size()==0) 
							throw new Exception("search result type not exist");
		//create sql
		String sql = transformSql(selectSqlToken,queryConfig.getParameterMapper(),para);
		List<QuestionFixToken> tokenList = getQuestionFixTokenList(selectSqlToken,para);
		return searchObjectMapData(connection,sql, tokenList, queryConfig.getParameterMapper(),resultMapper,para);
	}
	public <T> List<T> queryObjectMapData(Connection connection,String code,
			Map<String, Object> para,Class<T> clazz) throws Exception {
		List<Map<String,Object>> dataSet = queryObjectMapData(connection,code,para);
		List<T> dataT = new ArrayList<T>();
		for (Map<String, Object> map : dataSet) {
			dataT.add(BeanUtil.bindBean(clazz, map));
		}
		return dataT;
	}
	public PageDataSet<Map<String,Object>> queryPagingObjectMapData(Connection connection,String code,
			Map<String, Object> para) throws Exception{
		QueryConfig queryConfig = gainAndValidateQueryConfig(code, para);
		if(!queryConfig.isPaging()) throw new Exception("should invoke nonpaging method");
		int currentPage = 1;
		Object temp = para.get("page");
		if(temp!=null){
			if(temp instanceof String) currentPage = Integer.parseInt(temp.toString());
			else currentPage = (Integer)temp;
		}
		int limit = 10;
		temp = para.get("rows");
		if(temp!=null){
			if(temp instanceof String) limit = Integer.parseInt(temp.toString());
			else limit = (Integer)temp;
		}
		List<Token> selectSqlToken = getSelectSqlToken(queryConfig);
		if(selectSqlToken==null) throw new Exception("query sql is null");
		ResultMapper resultMapper = metaQuery.getResultMapper(queryConfig.getResultMapper());
		if(resultMapper==null) throw new Exception("not found ResultMapper for Query Config");
		List<ResultItem> resultItemList = resultMapper.getResultItemList();
		if(resultItemList==null || resultItemList.size()==0) 
							throw new Exception("search result type not exist");
		//create sql
		String sql = transformSql(selectSqlToken,queryConfig.getParameterMapper(),para);
		List<QuestionFixToken> questionTokenList = getQuestionFixTokenList(selectSqlToken,para);
		List<Token> countSqlToken = getCountSqlToken(queryConfig);
		int total = 0;
		if(countSqlToken==null){
			String countSql = "select count(*) from ("+sql+") T";
			total = getTotalCount(connection,countSql,questionTokenList,queryConfig.getParameterMapper(),para);
		}else{
			String countSql = transformSql(countSqlToken,queryConfig.getParameterMapper(),para);
			List<QuestionFixToken> countTokenList = getQuestionFixTokenList(countSqlToken,para);
			total = getTotalCount(connection,countSql,countTokenList,queryConfig.getParameterMapper(),para);
		}
		String pageSql = pagingSqlDecorator.decoratePagingSql(sql,limit,total,currentPage);
		if(pageSql.equals("")) pageSql=sql;
		List<Map<String,Object>> data = searchObjectMapData(connection,pageSql,questionTokenList,queryConfig.getParameterMapper(),resultMapper,para);
		PageDataSet<Map<String,Object>> dataSet = new PageDataSet<Map<String,Object>>(data);
		dataSet.setTotal(total);
		return dataSet;
	}
	public <T> PageDataSet<T> queryPagingObjectMapData(Connection connection,String code,
			Map<String, Object> para,Class<T> clazz) throws Exception{
		PageDataSet<Map<String,Object>> dataSet = queryPagingObjectMapData(connection,code,para);
		PageDataSet<T> dataT = new PageDataSet<T>();
		for (Map<String, Object> map : dataSet) {
			dataT.add(BeanUtil.bindBean(clazz, map));
		}
		dataT.setTotal(dataSet.getTotal());
		return dataT;
	}
	public List<String[]> queryStringArrayData(Connection connection,String code,
			Map<String, Object> para) throws Exception {
		QueryConfig queryConfig = gainAndValidateQueryConfig(code, para);
		if(queryConfig.isPaging()) throw new Exception("should invoke paging method");
		List<Token> selectSqlToken = getSelectSqlToken(queryConfig);
		if(selectSqlToken==null) throw new Exception("query sql is null");
		ResultMapper resultMapper = metaQuery.getResultMapper(queryConfig.getResultMapper());
		if(resultMapper==null) throw new Exception("not found ResultMapper for Query Config");
		//create sql
		String sql = transformSql(selectSqlToken,queryConfig.getParameterMapper(),para);
		List<QuestionFixToken> tokenList = getQuestionFixTokenList(selectSqlToken,para);
		return searchStringArrayData(connection,sql, tokenList, queryConfig.getParameterMapper(), resultMapper, para);
	}
	public List<Object[]> queryObjectArrayData(Connection connection,String code,
			Map<String, Object> para) throws Exception {
		QueryConfig queryConfig = gainAndValidateQueryConfig(code, para);
		if(queryConfig.isPaging()) throw new Exception("should invoke paging method");
		List<Token> selectSqlToken = getSelectSqlToken(queryConfig);
		if(selectSqlToken==null) throw new Exception("query sql is null");
		ResultMapper resultMapper = metaQuery.getResultMapper(queryConfig.getResultMapper());
		if(resultMapper==null) throw new Exception("not found ResultMapper for Query Config");
		List<ResultItem> resultItemList = resultMapper.getResultItemList();
		if(resultItemList==null || resultItemList.size()==0) 
							throw new Exception("search result type not exist");
		//create sql
		String sql = transformSql(selectSqlToken,queryConfig.getParameterMapper(),para);
		List<QuestionFixToken> tokenList = getQuestionFixTokenList(selectSqlToken,para);
		return searchObjectArrayData(connection,sql, tokenList, queryConfig.getParameterMapper(), resultMapper, para);
	}
	private QueryConfig gainAndValidateQueryConfig(String code,
			Map<String, Object> para) throws Exception {
		QueryConfig queryConfig = metaQuery.getQueryConfig(code);
		if(queryConfig==null) throw new Exception("code="+code+" is not exist");
		//check input parameter not empty
		List<ParameterItem> nonEmptyList=queryConfig.getParameterMapper().getNonEmptyParameterItem();
		if(nonEmptyList!=null){
			for (ParameterItem parameterItem : nonEmptyList) {
				if(para.get(parameterItem.getProperty())==null) 
					throw new Exception("输入参数"+parameterItem.getProperty()+"不能为空");
			}			
		}
		return queryConfig;
	}
	private String transformSql(List<Token> tokenList,
			ParameterMapper parameterMapper, Map<String, Object> para) {
		StringBuilder sb = new StringBuilder();
		for (Token token : tokenList) {
			sb.append(token.transform(para, parameterMapper));
		}
		return sb.toString();
	}
	private List<Token> getSelectSqlToken(QueryConfig queryConfig){
		if(queryConfig==null) return null;
		List<Token> tokenList = sqlTokenMap.get(queryConfig.getCode().concat("-select"));
		if(tokenList==null){
			PlaceHolderParser parser = new PlaceHolderParser();
			String filterSql = parser.filterWhiteSpace(queryConfig.getSelectSql());
			tokenList = parser.parsePlaceHolderString(filterSql);
			if(tokenList!=null){
				sqlTokenMap.put(queryConfig.getCode().concat("-select"), tokenList);
				//解析成符号-释放内存
				queryConfig.setSelectSql("");
			}
		}
		return tokenList;
	}
	private List<Token> getCountSqlToken(QueryConfig queryConfig){
		if(queryConfig==null) return null;
		List<Token> tokenList = sqlTokenMap.get(queryConfig.getCode().concat("-count"));
		if(tokenList==null){
			PlaceHolderParser parser = new PlaceHolderParser();
			String filterSql = parser.filterWhiteSpace(queryConfig.getCountSql());
			tokenList = parser.parsePlaceHolderString(filterSql);
			if(tokenList!=null){
				sqlTokenMap.put(queryConfig.getCode().concat("-count"),tokenList);
				//解析成符号-释放内存
				queryConfig.setCountSql("");
			}
		}
		return tokenList;
	}
	private List<QuestionFixToken> getQuestionFixTokenList(List<Token> tokenList,Map<String, Object> para){
		List<QuestionFixToken> list = new ArrayList<QuestionFixToken>();
		for (Token token : tokenList) {
			if(token.getTokenType()==Token.QUESTIONFIX_TOKEN){
				list.add((QuestionFixToken)token);
			}else if(token.getTokenType()==Token.BLOCK_TOKEN){
				BlockToken block = (BlockToken)token;
				list.addAll(block.getQuestionFixTokens(para));
			}
		}
		return list;
	}
	private int getTotalCount(Connection connection,String sql,List<QuestionFixToken> tokenList,
			ParameterMapper parameterMapper, Map<String, Object> para) throws SQLException {
		PreparedStatement ps = null;
		ResultSet resultset = null;
		int count = 0;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			for (int i = 0; i < tokenList.size(); i++) {
				QuestionFixToken token = tokenList.get(i);
				setParameterData(ps,i+1,parameterMapper.getParameterItem(token
						.getPlaceholderKey()),para.get(token.getPlaceholderKey()));
			}
			resultset = ps.executeQuery();
			if(resultset.next()){
				count = resultset.getInt(1);
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return count;
	}
	private List<Map<String, String>> searchStringMapData(Connection connection,String sql,
			List<QuestionFixToken> tokenList, ParameterMapper parameterMapper,
			ResultMapper resultMapper, Map<String, Object> para)
			throws SQLException {
		List<Map<String, String>> dataSet = new ArrayList<Map<String,String>>();
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			for (int i = 0; i < tokenList.size(); i++) {
				QuestionFixToken token = tokenList.get(i);
				setParameterData(ps,i+1,parameterMapper.getParameterItem(token
						.getPlaceholderKey()),para.get(token.getPlaceholderKey()));
			}
			resultset = ps.executeQuery();
			List<ResultItem> resultItemList = resultMapper.getResultItemList();
			if(resultItemList==null || resultItemList.size()==0){
				ResultSetMetaData rsmd = resultset.getMetaData();
				int colCount = rsmd.getColumnCount();
				String[] fields = new String[colCount];
				for (int col = 1; col <= colCount; col++)
					fields[col-1] = rsmd.getColumnName(col);
				while (resultset.next()) {
					dataSet.add(extractStringMapRowData(resultset,fields));
				}
			}else{
				while (resultset.next()) {
					dataSet.add(extractStringMapRowData(resultset,resultItemList));
				}
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return dataSet;
	}
	private List<Map<String,Object>> searchObjectMapData(Connection connection,String sql,
			List<QuestionFixToken> tokenList, ParameterMapper parameterMapper,
			ResultMapper resultMapper, Map<String, Object> para)
			throws SQLException {
		List<Map<String,Object>> dataSet = new ArrayList<Map<String,Object>>();
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			for (int i = 0; i < tokenList.size(); i++) {
				QuestionFixToken token = tokenList.get(i);
				setParameterData(ps,i+1,parameterMapper.getParameterItem(token
						.getPlaceholderKey()),para.get(token.getPlaceholderKey()));
			}
			resultset = ps.executeQuery();
			List<ResultItem> resultItemList = resultMapper.getResultItemList();
			while (resultset.next()) {
				dataSet.add(extractObjectMapRowData(resultset,resultItemList));
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return dataSet;
	}
	private List<String[]> searchStringArrayData(Connection connection,String sql,
			List<QuestionFixToken> tokenList, ParameterMapper parameterMapper,
			ResultMapper resultMapper, Map<String, Object> para)
			throws SQLException {
		List<String[]> dataSet = new ArrayList<String[]>();
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			for (int i = 0; i < tokenList.size(); i++) {
				QuestionFixToken token = tokenList.get(i);
				setParameterData(ps,i+1,parameterMapper.getParameterItem(token
						.getPlaceholderKey()),para.get(token.getPlaceholderKey()));
			}
			resultset = ps.executeQuery();
			List<ResultItem> resultItemList = resultMapper.getResultItemList();
			if(resultItemList==null || resultItemList.size()==0){
				ResultSetMetaData rsmd = resultset.getMetaData();
				int colCount = rsmd.getColumnCount();
				String[] fields = new String[colCount];
				for (int col = 1; col <= colCount; col++)
					fields[col-1] = rsmd.getColumnName(col);
				while (resultset.next()) {
					String[] value = new String[colCount];
					for (int i = 0; i < fields.length; i++) {
						value[i] = resultset.getString(fields[i]);
					}
					dataSet.add(value);
				}
			}else{
				while (resultset.next()) {
					dataSet.add(extractStringArrayRowData(resultset,resultItemList));
				}
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return dataSet;
	}
	private List<Object[]> searchObjectArrayData(Connection connection,String sql,
			List<QuestionFixToken> tokenList, ParameterMapper parameterMapper,
			ResultMapper resultMapper, Map<String, Object> para)
			throws SQLException {
		List<Object[]> dataSet = new ArrayList<Object[]>();
		PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			for (int i = 0; i < tokenList.size(); i++) {
				QuestionFixToken token = tokenList.get(i);
				setParameterData(ps,i+1,parameterMapper.getParameterItem(token
						.getPlaceholderKey()),para.get(token.getPlaceholderKey()));
			}
			resultset = ps.executeQuery();
			List<ResultItem> resultItemList = resultMapper.getResultItemList();
			while (resultset.next()) {
				dataSet.add(extractObjectArrayRowData(resultset,resultItemList));
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			JdbcUtils.closeResultSet(resultset);
			JdbcUtils.closeStatement(ps);
		}
		return dataSet;
	}
	
	private Map<String, Object> extractObjectMapRowData(ResultSet resultset,
			List<ResultItem> resultItemList) throws SQLException{
		Map<String, Object> map = new HashMap<String, Object>();
		for (ResultItem resultItem : resultItemList) {
			String columnLabel = resultItem.getColumn().length() == 0 ? 
					resultItem.getProperty() : resultItem.getColumn();
			String propertyKey = resultItem.getProperty().length() == 0 ? resultItem
					.getColumn() : resultItem.getProperty();
			AbstractType abstractType = TypeUtil.getDataType(resultItem.getType());
			map.put(propertyKey,abstractType.get(resultset, columnLabel));
		}
		return map;
	}
	
	private Map<String, String> extractStringMapRowData(ResultSet resultset,
			List<ResultItem> resultItemList) throws SQLException{
		Map<String, String> map = new HashMap<String, String>();
		for (ResultItem resultItem : resultItemList) {
			String columnLabel = resultItem.getColumn().length() == 0 ? 
					resultItem.getProperty() : resultItem.getColumn();
			String propertyKey = resultItem.getProperty().length() == 0 ? resultItem
					.getColumn() : resultItem.getProperty();
			if ("java.util.Date".equals(resultItem.getType())
					|| "java.sql.Date".equals(resultItem.getType())
					|| "java.sql.Timestamp".equals(resultItem.getType())) {
				Object value = resultset.getTimestamp(columnLabel);
				map.put(propertyKey,ConvertUtil.convertDateFormat(value,
								resultItem.getToStringformat()));
			}else{
				map.put(propertyKey,resultset.getString(columnLabel));
			}
		}
		return map;
	}
	private Map<String, String> extractStringMapRowData(ResultSet resultset,
			String[] fields) throws SQLException{
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < fields.length; i++) {
			map.put(fields[i], resultset.getString(fields[i]));
		}
		return map;
	}
	private String[] extractStringArrayRowData(ResultSet resultset,
			List<ResultItem> resultItemList) throws SQLException{
		String[] row = new String[resultItemList.size()];
		for (int i = 0; i < resultItemList.size(); i++) {
			ResultItem resultItem = resultItemList.get(i);
			String columnLabel = resultItem.getColumn().length() == 0 ? 
					resultItem.getProperty() : resultItem.getColumn();
			if ("java.util.Date".equals(resultItem.getType())
					|| "java.sql.Date".equals(resultItem.getType())
					|| "java.sql.Timestamp".equals(resultItem.getType())) {
				Timestamp timestamp = resultset.getTimestamp(columnLabel);
				row[i] = ConvertUtil.convertDateFormat(timestamp,
							resultItem.getToStringformat());
			}else{
				row[i] = resultset.getString(columnLabel);
			}
		}
		return row;
	}
	private Object[] extractObjectArrayRowData(ResultSet resultset,
			List<ResultItem> resultItemList) throws SQLException{
		Object[] row = new Object[resultItemList.size()];
		for (int i = 0; i < resultItemList.size(); i++) {
			ResultItem resultItem = resultItemList.get(i);
			String columnLabel = resultItem.getColumn().length() == 0 ? 
					resultItem.getProperty() : resultItem.getColumn();
			AbstractType abstractType = TypeUtil.getDataType(resultItem.getType());
			row[i] = abstractType.get(resultset, columnLabel);
		}
		return row;
	}
	private void setParameterData(PreparedStatement ps, int index,
			ParameterItem item, Object value) throws SQLException{
		String type = item==null ? "java.lang.String":item.getType();
		AbstractType abstractType = TypeUtil.getDataType(type);
		String format = item==null ? null:item.getToStringformat();
		abstractType.set(ps,ConvertUtil.convertToObject(type, value,format),index);
	}
	/**   below is set method area        */
	public void setQueryRegxFileName(String queryRegxFileName) {
		this.queryRegxFileName = queryRegxFileName;
	}
	public void setPagingSqlDecorator(PagingSqlDecorator pagingSqlDecorator) {
		this.pagingSqlDecorator = pagingSqlDecorator;
	}

}
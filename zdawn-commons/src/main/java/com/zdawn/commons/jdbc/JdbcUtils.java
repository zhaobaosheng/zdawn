package com.zdawn.commons.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.support.AbstractType;
import com.zdawn.commons.jdbc.support.TypeUtil;

public class JdbcUtils {
	private static final Logger log = LoggerFactory.getLogger(JdbcUtils.class);
	public static void closeStatement(Statement stmt){
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				log.error("closeStatement",e);
			}
	}
	public static void closeResultSet(ResultSet rs){
			try {
				if(rs!=null) rs.close();
			} catch (SQLException e) {
				log.error("closeSResultSet",e);
			}
	}
	public static void closeConnection(Connection connection){
		try {
			if(connection !=null) connection.close();
		} catch (Exception e) {
			log.error("closeConnection",e);
		}
	}
	/**
	 * 根据给定sql语句查询数据
	 * <br> 数据返回格式为字符串数组
	 * @param connection 数据库连接
	 * @param sql 语句
	 * @param para 输入对象参数
	 * @return 字符串数组 如果无数据返回 null
	 * @throws SQLException
	 */
	public static String[] searchOneRow(Connection connection,String sql,Object... para) throws SQLException{
		String[] fields = null;
		String[] value = null;
	    PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			if(para != null){
				for (int i = 0; i < para.length; i++) {
					if(para[i]==null) throw new SQLException("query parameter is null");
					AbstractType type = TypeUtil.getDataType(para[i].getClass().getName());
					type.set(ps, para[i], i+1);
				}
			}
			resultset = ps.executeQuery();
			ResultSetMetaData rsmd = resultset.getMetaData();
			int colCount = rsmd.getColumnCount(); //取得列数
			fields = new String[colCount];
			for (int col = 1; col <= colCount; col++)
				fields[col - 1] = rsmd.getColumnName(col);
			if(resultset.next()){
				value = new String[colCount];
				for (int i = 0; i < fields.length; i++) {
					value[i] = resultset.getString(fields[i]);
				}
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			closeResultSet(resultset);
			closeStatement(ps);
		}
		return value;
	}
	/**
	 * 根据sql语句查询数据
	 * <br>查询结果为字符串数组集合
	 * @param connection 数据库连接
	 * @param sql 语句
	 * @param para 输入对象参数
	 * @return 字符串数组集合 如果无数据返回 null
	 * @throws SQLException
	 */
	public static ArrayList<String[]> getSearchResult(Connection connection,String sql,Object... para) throws SQLException{
		String[] fields = null;
	    ArrayList<String[]> al = new ArrayList<String[]>();
	    PreparedStatement ps = null;
	    ResultSet resultset = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			if(para != null){
				for (int i = 0; i < para.length; i++) {
					if(para[i]==null) throw new SQLException("query parameter is null");
					AbstractType type = TypeUtil.getDataType(para[i].getClass().getName());
					type.set(ps, para[i], i+1);
				}
			}
			resultset = ps.executeQuery();
			ResultSetMetaData rsmd = resultset.getMetaData();
			int colCount = rsmd.getColumnCount(); //取得列数
			fields = new String[colCount];
			for (int col = 1; col <= colCount; col++)
				fields[col - 1] = rsmd.getColumnName(col);
			while (resultset.next()) {
				String[] value = new String[colCount];
				for (int i = 0; i < fields.length; i++) {
					value[i] = resultset.getString(fields[i]);
				}
				al.add(value);
			}
		} catch (SQLException e) {
		    throw e;
		}finally{
			closeResultSet(resultset);
			closeStatement(ps);
		}
		return al.size()==0 ? null:al;
	}
	/**
	 * 查询统计函数结果
	 * @param connection 数据连接
	 * @param sql 语句
	 * @param para 输入对象参数
	 * @return 统计函数结果 long
	 * @throws SQLException
	 */
	public static long getFunctionNumber (Connection connection,String sql,Object ... para) throws SQLException{
		long count = 0;
		PreparedStatement ps = null;
	    ResultSet rs = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			if(para != null){
				for (int i = 0; i < para.length; i++) {
					if(para[i]==null) throw new SQLException("query parameter is null");
					AbstractType type = TypeUtil.getDataType(para[i].getClass().getName());
					type.set(ps, para[i], i+1);
				}
			}
			rs = ps.executeQuery(sql);
			if(rs.next()){
			    count = rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		}finally{
			closeResultSet(rs);
			closeStatement(ps);
		}
		return count;
	}
	
	public static int getIntFunctionNumber (Connection connection,String sql,Object ... para) throws SQLException{
		return (int)getFunctionNumber(connection, sql, para);
	}
	/**
	 * 执行sql语句
	 * @param connection 数据库连接
	 * @param sql 语句
	 * @param para 输入对象参数
	 * @return 影响行数
	 * @throws SQLException
	 */
	public static int executeSql (Connection connection,String sql,Object ... para) throws SQLException{
		int count = 0;
		PreparedStatement ps = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			ps = connection.prepareStatement(sql);
			if(para != null){
				for (int i = 0; i < para.length; i++) {
					if(para[i]==null) throw new SQLException("query parameter is null");
					AbstractType type = TypeUtil.getDataType(para[i].getClass().getName());
					type.set(ps, para[i], i+1);
				}
			}
			count = ps.executeUpdate(sql);
		} catch (SQLException e) {
			throw e;
		}finally{
			closeStatement(ps);
		}
		return count;
	}
}

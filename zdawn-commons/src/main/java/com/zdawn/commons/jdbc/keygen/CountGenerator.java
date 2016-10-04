package com.zdawn.commons.jdbc.keygen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.JdbcUtils;
/**
 * 计数器 步长1
 * @author zhaobs
 *
 */
public class CountGenerator extends GeneratorAdapter {
	private  Logger log = LoggerFactory.getLogger(CountGenerator.class);
	
	private DataSource dataSource = null;
	
	private String lockSql = "select COUNT_NUM from SYS_RECORD_ID where ENTITY_NAME=? for update";
	
	@Override
	public String generateString(String entityName){
		return String.valueOf(generateLong(entityName));
	}
	
	@Override
	public Long generateLong(String entityName) {
		Connection con = null;
		Long id = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement stUpdate = null;
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			String updatesql = "update SYS_RECORD_ID set COUNT_NUM = COUNT_NUM + 1 where ENTITY_NAME='"+entityName+"'";
			st = con.prepareStatement(lockSql);
			st.setString(1, entityName);
			rs = st.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			stUpdate = con.prepareStatement(updatesql);
			if (stUpdate.executeUpdate() == 0) throw new Exception("update error");
			if(con!=null) con.commit();
		} catch (Exception e) {
			try {
				if(con!=null) con.rollback();
			} catch (SQLException e1) {}
			log.error("generate",e);
			throw new RuntimeException(e.toString());
		}finally{
			JdbcUtils.closeStatement(stUpdate);
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(st);
			JdbcUtils.setAutoCommit(con,true);
			JdbcUtils.closeConnection(con);
		}
		return id;
	}

	@Override
	public Integer generateInteger(String entityName) {
		return generateLong(entityName).intValue();
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLockSql(String lockSql) {
		this.lockSql = lockSql;
	}
}

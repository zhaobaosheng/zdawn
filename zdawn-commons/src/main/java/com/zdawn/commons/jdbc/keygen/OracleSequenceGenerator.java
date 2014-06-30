package com.zdawn.commons.jdbc.keygen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.JdbcUtils;

/**
 * 使用oracle序列生成编号
 * entityName为序列名称
 * @author zhaobs
 */
public class OracleSequenceGenerator extends GeneratorAdapter{
	private  Logger log = LoggerFactory.getLogger(OracleSequenceGenerator.class);
	
	private DataSource dataSource = null;
	
	@Override
	public String generateString(String entityName) {
		return String.valueOf(generateLong(entityName));
	}

	@Override
	public Long generateLong(String entityName) {
		Connection con = null;
		Long id = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql ="select "+entityName+".NEXTVAL from DUAL";
			con = dataSource.getConnection();
			st = con.prepareStatement(sql);
			rs = st.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
			}
		} catch (Exception e) {
			log.error("generate",e);
			throw new RuntimeException(e.toString());
		}finally{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(st);
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
}

package com.zdawn.commons.jdbc.keygen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zdawn.commons.jdbc.JdbcUtils;

/**
 * 建表语句
 *<br>create table SYS_RECORD_ID(ENTITY_NAME VARCHAR2(30) primary key,PRE_FIX VARCHAR2(4),COUNT_NUM NUMBER(10),COUNT_DATE DATE);
 *<br>comment on table SYS_RECORD_ID is '编号产生表';
 *<br>comment on column SYS_RECORD_ID.ENTITY_NAME  is '实体名';
 *<br>comment on column SYS_RECORD_ID.PRE_FIX  is '前缀';
 *<br>comment on column SYS_RECORD_ID.COUNT_NUM  is '计数值';
 *<br>comment on column SYS_RECORD_ID.COUNT_DATE  is '计数日期';
 * <br>规则：PRE_FIX+yyyyMMdd+6位流水号
 * <br>调用前在SYS_RECORD_ID表为每个实体名增加相应的数据
 * <br>eg: insert into SYS_RECORD_ID (ENTITY_NAME,PRE_FIX,COUNT_NUM)values('TB_APP','APP','1')
 * @author zhaobs
 *
 */
public class CodeGenerator extends GeneratorAdapter{
	private  Logger log = LoggerFactory.getLogger(CodeGenerator.class);
	private DataSource dataSource = null;
	private String lockSql = "select PRE_FIX,COUNT_NUM from SYS_RECORD_ID where ENTITY_NAME=? for update";
	
	@Override
	public String generateString(String entityName) {
		 String result = null;
		 String updatesql = "update SYS_RECORD_ID set COUNT_NUM = COUNT_NUM + 1 where ENTITY_NAME='"+entityName+"'";
		 Connection con = null;
		 PreparedStatement st = null;
		 ResultSet rs = null;
		 PreparedStatement stUpdate = null;
		 try {
			con = dataSource.getConnection();
			st = con.prepareStatement(lockSql);
			st.setString(1, entityName);
			rs = st.executeQuery();
			String prefix = "";
			int count = 0;
			if (rs.next()) {
				prefix = rs.getString(1);
				count = rs.getInt(2);
			}
			if(count>=999999) updatesql = "update SYS_RECORD_ID set COUNT_NUM = 1 where ENTITY_NAME='"+entityName+"'";
			stUpdate = con.prepareStatement(updatesql);
			if (stUpdate.executeUpdate() == 0) throw new Exception("update error");
			prefix = prefix == null ? "" : prefix;
			String code = "000000"+count;
			result = prefix + getDateString() + code.substring(code.length()-6);
			if(con!=null) con.commit();
		} catch (Exception e) {
			try {
				if(con!=null) con.rollback();
			} catch (SQLException e1) {}
			log.error("generateString",e);
			throw new RuntimeException(e.toString());
		}finally{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(st);
			JdbcUtils.closeStatement(stUpdate);
			JdbcUtils.closeConnection(con);
		}
		return result;
	}
	
	private String getDateString(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat df =new SimpleDateFormat("yyyyMMdd");
		return df.format(calendar.getTime());
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLockSql(String lockSql) {
		this.lockSql = lockSql;
	}
}

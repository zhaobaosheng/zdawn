package com.zdawn.commons.jdbc.keygen;

import java.sql.Connection;
import java.sql.Date;
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
 * 通用标识生成器
 * <br>使用前在SYS_RECORD_ID表插入数据
 * <br>调用时输入参数是实体名称即主键
 * <br>使用需要注入参数
 *     dataSource-必须注入
 *     lockSql-可选 oracle不需要配置
 *     serialNumberMaxValue-可选 默认9999
 * <br>规则PRE_FIX+yyyMMdd(生成当前日期)+n位流水号（每日归零）
 * @author zhaobs
 *
 */
public class CommonPrefixIDGenerator extends GeneratorAdapter {
	private  Logger log = LoggerFactory.getLogger(CommonPrefixIDGenerator.class);
	/**
	 * 流水号做大值
	 */
	private int serialNumberMaxValue = 9999;
	
	private String zeroSupport = null;
	
	private DataSource dataSource = null;
	
	private String lockSql = "select PRE_FIX,COUNT_NUM,COUNT_DATE from SYS_RECORD_ID where ENTITY_NAME=? for update";
	
	@Override
	public String generateString(String entityName){
		Connection con = null;
		String id = null;
		try {
			con = dataSource.getConnection();
			id = generate(con,entityName);
		} catch (RuntimeException e) {
			throw e;
		}catch (Exception e) {
			log.error("generateString",e);
			throw new RuntimeException(e.toString());
		}finally{
			JdbcUtils.closeConnection(con);
		}
		return id;
	}

	public String generate(Connection con, String entityName){
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement stUpdate = null;
		String id = null;
		try {
			con.setAutoCommit(false);
			String updatesql = "update SYS_RECORD_ID set COUNT_NUM = COUNT_NUM + 1 where ENTITY_NAME=?";
			st = con.prepareStatement(lockSql);
			st.setString(1, entityName);
			int count = 0;
			String prefix = "";
			Date countDate = null;
			Calendar calendar = Calendar.getInstance();
			rs = st.executeQuery();
			if (rs.next()) {
				count = rs.getInt(2);
				prefix = rs.getString(1);
				prefix = prefix==null ? "":prefix;
				countDate=  rs.getDate(3);
			}
			
			if(sameDate(countDate,calendar)){
				if(count>serialNumberMaxValue) throw new Exception("exceed max value");
				stUpdate = con.prepareStatement(updatesql);
				stUpdate.setString(1, entityName);
			}else{
				updatesql = "update SYS_RECORD_ID set COUNT_NUM = 2,COUNT_DATE=? where ENTITY_NAME=?";
				stUpdate = con.prepareStatement(updatesql);
				stUpdate.setDate(1,new Date(calendar.getTimeInMillis()));
				stUpdate.setString(2,entityName);
				count =1;
			}
			if (stUpdate.executeUpdate() == 0) throw new Exception("update error");
			id = prefix+getDateString(calendar)+produceNumberMaxValue(count);
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
		}
		return id;
	}
	private String produceNumberMaxValue(int count) {
		if(zeroSupport==null){
			String intMaxString = "000000000";
			zeroSupport = intMaxString.substring(0,String.valueOf(serialNumberMaxValue).length());
		}
		String code = zeroSupport+count;
		return code.substring(code.length()-zeroSupport.length());
	}

	private String getDateString(Calendar calendar){
		SimpleDateFormat df =new SimpleDateFormat("yyyyMMdd");
		return df.format(calendar.getTime());
	}
	
	private boolean sameDate(Date countDate, Calendar current){
		if(countDate==null) return false;
		long diff = Math.abs(countDate.getTime()-current.getTimeInMillis());
		return diff>=0 && diff<3600*24*1000;
	}

	public int getSerialNumberMaxValue() {
		return serialNumberMaxValue;
	}

	public void setSerialNumberMaxValue(int serialNumberMaxValue) {
		this.serialNumberMaxValue = serialNumberMaxValue;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getLockSql() {
		return lockSql;
	}

	public void setLockSql(String lockSql) {
		this.lockSql = lockSql;
	}
	
}

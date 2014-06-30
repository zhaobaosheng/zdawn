package com.zdawn.commons.jdbc.support;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DateType extends AbstractType {

	public Object get(ResultSet rs, String name) throws SQLException {
		return rs.getDate(name);
	}

	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getDate(index);
	}

	public void set(PreparedStatement ps, Object value, int index)
			throws SQLException {
		if(value!=null){
			ps.setDate(index,(Date)value);
		}else{
			ps.setNull(index,Types.DATE);
		}
	}
}

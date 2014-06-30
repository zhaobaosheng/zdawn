package com.zdawn.commons.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FloatType extends AbstractType {

	public Object get(ResultSet rs, String name) throws SQLException {
		return rs.getFloat(name);
	}

	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getFloat(index);
	}

	public void set(PreparedStatement ps, Object value, int index)
			throws SQLException {
		if(value!=null){
			ps.setFloat(index,(Float)value);
		}else{
			ps.setNull(index,Types.FLOAT);
		}
	}

}

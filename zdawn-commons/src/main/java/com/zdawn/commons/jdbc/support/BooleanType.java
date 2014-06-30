package com.zdawn.commons.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanType extends AbstractType {

	public Object get(ResultSet rs, String name) throws SQLException {
		return rs.getBoolean(name);
	}

	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getBoolean(index);
	}

	public void set(PreparedStatement ps, Object value, int index)
			throws SQLException {
		boolean result = value==null ? false:(Boolean)value;
		ps.setBoolean(index, result);
	}

}

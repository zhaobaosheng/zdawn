package com.zdawn.commons.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ShortType extends AbstractType {

	public Object get(ResultSet rs, String name) throws SQLException {
		return rs.getShort(name);
	}

	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getShort(index);
	}

	public void set(PreparedStatement ps, Object value, int index)
			throws SQLException {
		if(value!=null){
			ps.setShort(index,(Short)value);
		}else{
			ps.setNull(index,Types.SMALLINT);
		}
	}

}

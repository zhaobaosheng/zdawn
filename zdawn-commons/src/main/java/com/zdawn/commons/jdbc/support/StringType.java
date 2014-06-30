package com.zdawn.commons.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * create time 2006-5-16
 * @author nbs
 */
public class StringType extends AbstractType {
	
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getString(index);
	}
	public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getString(name);
    }
    public void set(PreparedStatement ps, Object value, int index)
            throws SQLException {
    	if(value!=null){
			ps.setString(index, value.toString());
		}else{
			ps.setNull(index,Types.VARCHAR);
		}
    }
}

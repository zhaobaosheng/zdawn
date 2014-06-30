package com.zdawn.commons.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * create time 2006-5-16
 * @author nbs
 */
public class TimestampType extends AbstractType {

	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getTimestamp(index);
	}
	public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getTimestamp(name);
    }
	
    public void set(PreparedStatement ps, Object value, int index)
            throws SQLException {
    	if(value!=null){
			ps.setTimestamp(index,(Timestamp)value);
		}else{
			ps.setNull(index,Types.TIMESTAMP);
		}
    }
}

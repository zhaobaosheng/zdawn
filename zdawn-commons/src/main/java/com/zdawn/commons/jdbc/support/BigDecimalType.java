package com.zdawn.commons.jdbc.support;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * create time 2006-5-16
 * @author nbs
 */
public class BigDecimalType extends AbstractType {
	
	public Object get(ResultSet rs, int index) throws SQLException {
		return rs.getBigDecimal(index);
	}
	public Object get(ResultSet rs, String name) throws SQLException {
        return rs.getBigDecimal(name);
    }
    public void set(PreparedStatement ps, Object value, int index)
            throws SQLException {
    	if(value!=null){
			ps.setBigDecimal(index,(BigDecimal)value);
		}else{
			ps.setNull(index,Types.NUMERIC);
		}
    }
}

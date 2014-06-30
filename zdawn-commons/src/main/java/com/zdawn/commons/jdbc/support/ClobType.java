package com.zdawn.commons.jdbc.support;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClobType extends AbstractType {
	
	public static final int STREAM_TO_CLOB = 1;
	
	public static final int STRING_TO_CLOB = 2;
	
	private int current = 2;
	
	public ClobType(){
	}
	public ClobType(int type){
		current = type;
	}
	@Override
	public Object get(ResultSet rs, String name) throws SQLException {
		String value = "";
		if(current==ClobType.STRING_TO_CLOB){
			Clob clob = rs.getClob(name);
		    if(clob != null) {
		      int size = (int) clob.length();
		      value = clob.getSubString(1, size);
		    }
		}
	    return value;
	}

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		String value = "";
		if(current==ClobType.STRING_TO_CLOB){
			Clob clob = rs.getClob(index);
		    if(clob != null) {
		      int size = (int) clob.length();
		      value = clob.getSubString(1, size);
		    }
		}
	    return value;
	}

	@Override
	public void set(PreparedStatement ps, Object value, int index)
			throws SQLException {
		if(current==ClobType.STRING_TO_CLOB){
			if(value!=null){
				String data = value.toString();
				StringReader reader = new StringReader(data);
			    ps.setCharacterStream(index, reader, data.length());
			}
		}else if(current==ClobType.STREAM_TO_CLOB){
			Reader reader = (Reader)value;
			//jdbc4 method
			ps.setCharacterStream(index,reader);
		}else throw new SQLException("can not dispose type");
	}

}

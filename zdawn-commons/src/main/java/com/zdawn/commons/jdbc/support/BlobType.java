package com.zdawn.commons.jdbc.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobType extends AbstractType {
	
	public static final int STREAM_TO_BLOB = 1;
	
	public static final int BYTEARRAY_TO_BLOB = 2;
	
	private int current = 2;
	
	public BlobType(){
	}
	public BlobType(int type){
		current = type;
	}
	@Override
	public Object get(ResultSet rs, String name) throws SQLException {
		Object returnValue = null;
		if(current==BlobType.BYTEARRAY_TO_BLOB){
			Blob blob = rs.getBlob(name);
		    if (null != blob) {
		      returnValue = blob.getBytes(1, (int) blob.length());
		    }
		}
		return returnValue;
	}

	@Override
	public Object get(ResultSet rs, int index) throws SQLException {
		Object returnValue = null;
		if(current==BlobType.BYTEARRAY_TO_BLOB){
			Blob blob = rs.getBlob(index);
		    if (null != blob) {
		      returnValue = blob.getBytes(1, (int) blob.length());
		    }
		}
		return returnValue;
	}

	@Override
	public void set(PreparedStatement ps, Object value, int index)
			throws SQLException {
		if(current==BlobType.BYTEARRAY_TO_BLOB){
			byte[] data = (byte[])value;
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			//jdbc3 method
		    ps.setBinaryStream(index, bis, data.length);
		}else if(current==BlobType.STREAM_TO_BLOB){
			InputStream in = (InputStream)value;
			//jdbc4 method
			ps.setBinaryStream(index,in);
		}else throw new SQLException("can not dispose type");
	}

}

package com.zdawn.commons.sysmodel.persistence;

import com.zdawn.DawnException;

public class PersistenceException extends DawnException {
	private static final long serialVersionUID = -8978553900913403279L;
	public PersistenceException(String code,String message){
		super(code,message);
	}
}

package com.zdawn;

public class DawnException extends Exception {
	private static final long serialVersionUID = -7632485704387680288L;
	/**
	 * 异常标识
	 */
	private String code = "";
	/**
	 * 异常错误信息类
	 */
	private Object errorObject = null;
	
	public DawnException(String code,String message){
		super(message);
		this.code = code;
	}

	@Override
	public String toString() {
		return code.concat("-"+super.getMessage());
	}

	public Object getErrorObject() {
		return errorObject;
	}

	public void setErrorObject(Object errorObject) {
		this.errorObject = errorObject;
	}
	
}

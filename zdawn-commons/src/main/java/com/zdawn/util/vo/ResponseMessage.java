package com.zdawn.util.vo;

import java.io.Serializable;

public class ResponseMessage implements Serializable {
	private static final long serialVersionUID = 1135623201756577996L;
	private String result = "";
	private String desc = "";
	private Object data = null;

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}

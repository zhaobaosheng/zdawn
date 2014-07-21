package com.zdawn.commons.dict.model;

import java.io.Serializable;

public class PropertyValue implements Serializable {
	private static final long serialVersionUID = -4067115880402126208L;
	/**
	 * 键
	 */
	private String id = null;
	/**
	 * 值
	 */
	private String text = null;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
